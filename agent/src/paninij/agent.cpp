#include <jvmti.h>
#include <iostream>
#include <cassert>
#include "paninij/agent.h"
#include "paninij/ownership.h"

/*****************************************************************************
 * Global Symbolic Constants                                                 *
 *****************************************************************************/

namespace {
    /**
     * Indicates that a heap traversal should not limit callbacks based on
     * whether an object or its class is tagged.
     *
     * @see
     *    <a href="https://docs.oracle.com/javase/8/docs/platform/jvmti/jvmti.html#jvmtiHeapFilter">Heap Filter Flags</a>
     */
    const jint DO_NOT_FILTER_HEAP_CALLBACKS_BY_TAG = 0;

    /**
     * Indicates that a heap traversal should not limit callbacks based on
     * an object's class.
     *
     * @see
     *     <a href="https://docs.oracle.com/javase/8/docs/platform/jvmti/jvmti.html#FollowReferences.klass">
     *       FollowReferences.klass
     *      </a>
     */
    const jclass DO_NOT_FILTER_HEAP_CALLBACKS_BY_CLASS = nullptr;

    /**
     * Indicates that heap traversal should continue, but not visit any objects
     * referenced by the object currently being visited.
     *
     * @see
     *     <a href="https://docs.oracle.com/javase/8/docs/platform/jvmti/jvmti.html#jvmtiHeapVisitControl">
     *       Heap Visit Control Flags
     *     </a>
     */
    const jint DO_NOT_VISIT_OBJECTS = ! JVMTI_VISIT_ABORT
                                    & ! JVMTI_VISIT_OBJECTS;

    /** Indicates that an object has no tag. */
    const jlong NO_TAG = 0;

    /** A heap tag which indicates that an object is being moved. */
    const jlong MOVE_TAG = 1;

    /**
     * A heap tag which indicates that an owned object was found to be illegally
     * moved: this moved object it is still owned-by (i.e. reachable-from) the
     * sender.
     */
    const jlong ILLEGAL_MOVE_TAG = 2;

    /**
     * Passed to various JVM-TI functions to indicates that the current thread
     * should be used.
     */
    const jthread CURRENT_THREAD = NULL;
}


/*****************************************************************************
 * Agent Global State                                                        *
 *****************************************************************************/

jvmtiEnv* jvmti_env;       // TODO: Is there anything unsafe about storing this?


/*****************************************************************************
 * Agent Initialization                                                      *
 *****************************************************************************/

JNIEXPORT jint JNICALL
Agent_OnLoad(JavaVM* jvm, char* options, void* reserved) {
    jvmtiEnv* env;

    if (JNI_EVERSION == jvm->GetEnv((void **) &env, JVMTI_VERSION_1_2)) {
        env = nullptr;
        return JNI_ERR;
    }
    if (! add_capabilities(env) ||
        ! enable_events(env) ||
        ! set_event_callbacks(env))
    {
        jvmtiError err = env->DisposeEnvironment();
        // TODO: Check `err`?

        env = nullptr;
        return JNI_ERR;
    }
    jvmti_env = env;
    return JNI_OK;
}


bool add_capabilities(jvmtiEnv* env) {
    jvmtiCapabilities potential;
    jvmtiError err = env->GetPotentialCapabilities(&potential);
    if (err != JVMTI_ERROR_NONE) return false;
    if (! potential.can_tag_objects) return false;
    if (! potential.can_generate_method_entry_events) return false;

    env->AddCapabilities(&agent_capabilities);

    return true;
}


bool enable_events(jvmtiEnv* env) {
    jthread t = nullptr;
    for (jvmtiEvent& ev : enabled_events) {
        jvmtiError err = env->SetEventNotificationMode(JVMTI_ENABLE, ev, t);
        if (err != JVMTI_ERROR_NONE) {
            return false;
        }
    }
    return true;
}


bool set_event_callbacks(jvmtiEnv* env) {
    jvmtiError err = env->SetEventCallbacks(&agent_callbacks,
                                            sizeof(jvmtiEventCallbacks));
    return err == JVMTI_ERROR_NONE;
}


/*****************************************************************************
 * Agent Callbacks                                                           *
 *****************************************************************************/

static void JNICALL
vm_object_alloc_cb(jvmtiEnv *jvmti_env, JNIEnv* jni_env, jthread thread,
                   jobject object, jclass object_klass, jlong size) {
    std::cout << "vm_object_alloc_cb()" << std::endl;
}


static jint JNICALL
heap_tagging_cb(jvmtiHeapReferenceKind reference_kind,
                  const jvmtiHeapReferenceInfo* reference_info,
                  jlong, // class_tag
                  jlong, // referrer_class_tag
                  jlong, // size
                  jlong* tag_ptr,
                  jlong* referrer_tag_ptr,
                  jint,  // length
                  void*) // user_data
{
    if (referrer_tag_ptr == nullptr) {
        return JVMTI_VISIT_OBJECTS;
    } else if (reference_kind == JVMTI_HEAP_REFERENCE_CLASS) {
        return DO_NOT_VISIT_OBJECTS;
    } else {
        *tag_ptr = *referrer_tag_ptr;
        return JVMTI_VISIT_OBJECTS;
    }
}


static jint JNICALL
heap_searching_cb(jvmtiHeapReferenceKind reference_kind,
                  const jvmtiHeapReferenceInfo* reference_info,
                  jlong,  // class_tag
                  jlong,  // referrer_class_tag
                  jlong,  // size
                  jlong*  tag_ptr,
                  jlong*, //referrer_tag_ptr
                  jint,   // length
                  void* found_illegal_move)
{
    if (reference_kind == JVMTI_HEAP_REFERENCE_CLASS) {
        return DO_NOT_VISIT_OBJECTS;
    }
    if (*tag_ptr == MOVE_TAG) {
        *tag_ptr = ILLEGAL_MOVE_TAG;
        *(bool*) found_illegal_move = true;
        return JVMTI_VISIT_ABORT;
    }
    return JVMTI_VISIT_OBJECTS;
}


/*****************************************************************************
 * Agent Helper Functions                                                    *
 *****************************************************************************/

void throw_new(JNIEnv* jni_env, const char* cls_name, const char* msg) {
    jclass cls = jni_env->FindClass(cls_name);
    jni_env->ThrowNew(cls, msg);
}

jvmtiError tag_reachable(jobject root, jlong tag) {
    jvmtiError err;

    err = jvmti_env->SetTag(root, tag);
    if (err != JVMTI_ERROR_NONE) return err;

    err = jvmti_env->FollowReferences(DO_NOT_FILTER_HEAP_CALLBACKS_BY_TAG,
                                      DO_NOT_FILTER_HEAP_CALLBACKS_BY_CLASS,
                                      root, &heap_tagging_callbacks, nullptr);
    if (err != JVMTI_ERROR_NONE) return err;

    return JVMTI_ERROR_NONE;
}

jobject get_all_state(JNIEnv* jni_env, jobject sender) {
    jclass cls = jni_env->GetObjectClass(sender);
    jmethodID mid = jni_env->GetMethodID(cls, "panini$getAllState",
                                              "()Ljava/lang/Object;");
    assert(mid != NULL);  // A capsule should have a `panini$getAllState()`.
    return jni_env->CallObjectMethod(sender, mid);
}


/**
 * Starts seeking the current thread's stack at the given {@code depth} to find
 * the depth of the first stack frame whose instance (a.k.a. receiver variable,
 * {@code this}) is the same as the {@code target_inst}. This method may modify
 * the pointee of {@code depth}. If the frame indicated by the initial value of
 * {@code *depth} has a local instance equal to {@code target_inst}, then
 * {@code *depth} will not be changed.
 *
 * @param depth
 *      The depth at which to start the search. Upon return from this method,
 *      this is the depth at which the first frame with this {@code target_inst}
 *      was found. The value of the pointee is undefined if the method returns
 *      anything besides {@code JVMTI_ERROR_NONE}.
 *
 * @returns
 *      {@code JVMTI_ERROR_NONE} if some stack frame of the current thread was
 *      found to have {@code target_inst} as its instance. {@code
 *      JVMTI_ERROR_NO_MORE_FRAMES} if no such stack frame is found.
 */
jvmtiError find_first_frame_with_instance(JNIEnv *jni_env,
                                          jobject target_inst,
                                          jint *depth) {
    jvmtiError err;
    jobject inst;

    for ( ; ; (*depth)++) {
        inst = nullptr;
        err = jvmti_env->GetLocalInstance(CURRENT_THREAD, *depth, &inst);
        switch (err) {
            case JVMTI_ERROR_NONE:
                if (jni_env->IsSameObject(inst, target_inst))
                    return JVMTI_ERROR_NONE;
                else
                    continue;
            case JVMTI_ERROR_INVALID_SLOT:  // Method is static.
            case JVMTI_ERROR_OPAQUE_FRAME:  // Method is native.
                continue;
            case JVMTI_ERROR_MUST_POSSESS_CAPABILITY:
            case JVMTI_ERROR_INVALID_THREAD:
            case JVMTI_ERROR_THREAD_NOT_ALIVE:
            case JVMTI_ERROR_ILLEGAL_ARGUMENT:
            case JVMTI_ERROR_NO_MORE_FRAMES:
            case JVMTI_ERROR_NULL_POINTER:
            default:
                return err;
        }
    }
}


/**
 * Scans upwards through the stack (from more recent to less recent) looking for
 * the span of stack frames on the current thread whose instance variable
 * (a.k.a. receiver parameter or {@code this}) is the same object as the
 * {@code target_inst}.
 *
 * @returns
 *      {@code JVMTI_ERROR_NONE} iff successful.
 */
jvmtiError find_frame_span_with_instance(JNIEnv *jni_env, jobject target_inst,
                                         jint* start_depth, jint* num_frames) {
    jvmtiError err;
    jobject inst = nullptr;

    *start_depth = 0;
    *num_frames = 0;

    // Find the start of the span.
    err = find_first_frame_with_instance(jni_env, target_inst, start_depth);
    if (err != JVMTI_ERROR_NONE) return err;

    // Find the number of frames in the span:
    for (*num_frames = 1; true; (*num_frames)++) {
        err = jvmti_env->GetLocalInstance(CURRENT_THREAD,
                                          *start_depth + *num_frames,
                                          &inst);
        switch (err) {
            case JVMTI_ERROR_NONE:
                if (jni_env->IsSameObject(inst, target_inst)) {
                    continue;  // Still in the span.
                } else {
                    return JVMTI_ERROR_NONE;  // Found the end of the span.
                }
            default:
                return err;
        }
    }
}


jvmtiError tag_reachable_as_moved(jobjectArray root) {
    return tag_reachable(root, MOVE_TAG);
}


jvmtiError untag_reachable(jobjectArray root) {
    return tag_reachable(root, NO_TAG);
}


/**
 * Sets {@code mid} to be the method ID of the stack frame of the given thread
 * at the given depth.
 */
jvmtiError get_method_id(jthread thread, jint depth, jmethodID* mid) {
    static const jsize MAX_NUM_FRAMES = 1;
    jvmtiError err;

    jvmtiFrameInfo frames[MAX_NUM_FRAMES];
    jint num_frames;
    err = jvmti_env->GetStackTrace(thread, depth, MAX_NUM_FRAMES,
                                   frames, &num_frames);
    if (err != JVMTI_ERROR_NONE) return err;
    if (num_frames == 0) return JVMTI_ERROR_NO_MORE_FRAMES;

    *mid = frames[0].method;
    return JVMTI_ERROR_NONE;
}


jvmtiError deallocate_local_variable_table(jvmtiLocalVariableEntry* table,
                                           jint size)
{
    jvmtiError err;

    for (jvmtiLocalVariableEntry* e = table; e < table + size; e++) {
        err = jvmti_env->Deallocate((unsigned char *) e->name);
        if (err != JVMTI_ERROR_NONE) return err;

        err = jvmti_env->Deallocate((unsigned char *) e->signature);
        if (err != JVMTI_ERROR_NONE) return err;

        err = jvmti_env->Deallocate((unsigned char *) e->generic_signature);
        if (err != JVMTI_ERROR_NONE) return err;
    }

    err = jvmti_env->Deallocate((unsigned char *) table);
    if (err != JVMTI_ERROR_NONE) return err;

    return JVMTI_ERROR_NONE;
}


/*****************************************************************************
 * Functions which Drive Ownership Transfer Checks                           *
 *****************************************************************************/

jvmtiError check_reachable_from_object(JNIEnv* jni_env, jobject obj,
                                       bool* found_illegal_move) {
    jvmtiError err;

    if (jni_env->IsSameObject(obj, nullptr)) {
        return JVMTI_ERROR_NONE;
    }

    jlong tag = NO_TAG;
    err = jvmti_env->GetTag(obj, &tag);
    if (err != JVMTI_ERROR_NONE) return err;

    if (tag == MOVE_TAG) {
        *found_illegal_move = true;
        err = jvmti_env->SetTag(obj, ILLEGAL_MOVE_TAG);
        if (err != JVMTI_ERROR_NONE) return err;
    } else {
        err = jvmti_env->FollowReferences(DO_NOT_FILTER_HEAP_CALLBACKS_BY_TAG,
                                          DO_NOT_FILTER_HEAP_CALLBACKS_BY_CLASS,
                                          obj,
                                          &heap_searching_callbacks,
                                          &found_illegal_move);
        if (err != JVMTI_ERROR_NONE) return err;
    }

    return JVMTI_ERROR_NONE;
}

jvmtiError check_reachable_from_stack_local(JNIEnv* jni_env, jint depth,
                                            jint slot, bool* found_illegal_move)
{
    jvmtiError err;

    // TODO: What about reused slots (e.g. locals with disjoint lifetimes)?
    jobject obj = nullptr;
    err = jvmti_env->GetLocalObject(CURRENT_THREAD, depth, slot, &obj);
    if (err != JVMTI_ERROR_NONE) return err;

    return check_reachable_from_object(jni_env, obj, found_illegal_move);
}


/**
 * Checks all objects reachable from a capsule's stack frame for illegal
 * ownership moves.
 *
 * Precondition: All moved objects are already tagged.
 * Precondition: {@code depth} points to a frame whose corresponding method is a
 *               method for of a capsule template.
 */
jvmtiError check_reachable_from_stack_frame(JNIEnv* jni_env, jint depth,
                                            bool* found_illegal_move)
{
    jvmtiError err;

    jmethodID mid;
    err = get_method_id(CURRENT_THREAD, depth, &mid);
    if (err != JVMTI_ERROR_NONE) return err;

    jvmtiLocalVariableEntry* table = nullptr;
    jint size = 0;
    err = jvmti_env->GetLocalVariableTable(mid, &size, &table);
    if (err != JVMTI_ERROR_NONE) return err;

    for (jvmtiLocalVariableEntry* e = table; e < table + size; e++) {
        if (e->slot == 0) {
            continue;  // Skip the instance (a.k.a. receiver or `this` variable)
        }

        // TODO: Preemptively use `e->signature` to check if local has ref type.
        err = check_reachable_from_stack_local(jni_env, depth, e->slot,
                                               found_illegal_move);
        switch (err) {
            case JVMTI_ERROR_NONE:           // Check completed normally.
            case JVMTI_ERROR_TYPE_MISMATCH:  // Local var isn't of ref type.
                continue;
            default:                         // Some other error occurred.
                return err;
        }
    }

    err = deallocate_local_variable_table(table, size);
    if (err != JVMTI_ERROR_NONE) return err;

    return JVMTI_ERROR_NONE;
}


/**
 * Checks all objects reachable from the capsule's stack for illegal ownership
 * moves.
 *
 * Precondition: All moved objects are already tagged.
 */
jvmtiError check_reachable_from_stack(JNIEnv* jni_env, jobject sender_encap)
{
    jvmtiError err;
    bool found_illegal_move = false;

    jint start_depth;
    jint num_frames;

    err = find_frame_span_with_instance(jni_env, sender_encap,
                                        &start_depth, &num_frames);
    if (err != JVMTI_ERROR_NONE) return err;

    for (int depth = start_depth; depth < start_depth + num_frames; depth++) {
        err = check_reachable_from_stack_frame(jni_env, depth,
                                               &found_illegal_move);
        if (err != JVMTI_ERROR_NONE) return err;
    }

    if (found_illegal_move) {
        // TODO: Consider using `GetObjectsWithTags()` for better err reporting.
        throw_new(jni_env, "org/paninij/runtime/check/OwnershipMoveError",
                  "Detected an illegal ownership move.");
    }

    return JVMTI_ERROR_NONE;
}


/**
 * Checks all objects reachable from the capsule's states for illegal ownership
 * moves.
 *
 * Precondition: All moved objects are already tagged.
 */
jvmtiError check_reachable_from_states(JNIEnv *jni_env, jobject sender) {
    jvmtiError err;
    bool found_illegal_move = false;

    // Search for objs reachable from `sender_state` but marked with `MOVE_TAG`.
    jobject sender_state = get_all_state(jni_env, sender);
    err = jvmti_env->FollowReferences(DO_NOT_FILTER_HEAP_CALLBACKS_BY_TAG,
                                      DO_NOT_FILTER_HEAP_CALLBACKS_BY_CLASS,
                                      sender_state,
                                      &heap_searching_callbacks,
                                      &found_illegal_move);
    if (err != JVMTI_ERROR_NONE) return err;

    if (found_illegal_move) {
        // TODO: Consider using `GetObjectsWithTags()` for better err reporting.
        throw_new(jni_env, "org/paninij/runtime/check/OwnershipMoveError",
                           "Detected an illegal ownership move.");
    }

    return JVMTI_ERROR_NONE;
}




/*****************************************************************************
 * Agent Shutdown                                                            *
 *****************************************************************************/

JNIEXPORT void JNICALL
Agent_OnUnload(JavaVM *vm) {
    // Nothing to do yet.
}


/*****************************************************************************
 * Definitions of JNI Methods                                                *
 *****************************************************************************/

/**
 * This is called by the PaniniJ runtime to report that the client's program has
 * moved ownership of the object graph rooted at `ref` from the `sender` capsule
 * to the `receiver` capsule. The given `clazz` is always the `Ownership` class
 * itself.
 */
JNIEXPORT void JNICALL
Java_org_paninij_runtime_check_Ownership_procedureInvocationMove (
    JNIEnv* jni_env,
    jclass,  // Ownership.class
    jobject  sender,
    jobject  sender_encap,
    jobject, // receiver (currently unused)
    jobjectArray moved
) {
    assert(sender != nullptr
        && sender_encap != nullptr
        && moved != nullptr);

    // Return early if nothing is being moved.
    if (jni_env->GetArrayLength(moved) == 0) {
        return;
    }

    // Notice that under the assumption of strong ownership and state
    // encapsulation, there are no data-races between these different calls to
    // `FollowReferences()`. This is because under these assumptions, the only
    // Java thread which may modify the `sender` or `moved` object graphs is the
    // Java thread which called this JNI method.

    jvmtiError err;

    err = tag_reachable_as_moved(moved);
    assert (err == JVMTI_ERROR_NONE);

    err = check_reachable_from_stack(jni_env, sender_encap);
    assert (err == JVMTI_ERROR_NONE);

    err = check_reachable_from_states(jni_env, sender);
    assert (err == JVMTI_ERROR_NONE);

    untag_reachable(moved);
}

JNIEXPORT void JNICALL
Java_org_paninij_runtime_check_Ownership_procedureReturnMove (
        JNIEnv* jni_env,
        jclass, // Ownership.class
        jobject sender,
        jobject sender_encapsulated,
        jobject moved
) {
    throw_new(jni_env, "java/lang/UnsupportedOperationException",
                       "TODO: Everything!");
}

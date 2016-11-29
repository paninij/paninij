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

void tag_all_reachable(jobject root, jlong tag) {
    jvmtiError err;
    err = jvmti_env->SetTag(root, tag);
    assert(err == JVMTI_ERROR_NONE);
    err = jvmti_env->FollowReferences(DO_NOT_FILTER_HEAP_CALLBACKS_BY_TAG,
                                      DO_NOT_FILTER_HEAP_CALLBACKS_BY_CLASS,
                                      root, &heap_tagging_callbacks, nullptr);
    assert(err == JVMTI_ERROR_NONE);
}

jobject get_all_state(JNIEnv* jni_env, jobject sender) {
    jclass cls = jni_env->GetObjectClass(sender);
    jmethodID mid = jni_env->GetMethodID(cls, "panini$getAllState",
                                         "()Ljava/lang/Object;");
    assert(mid != NULL);  // Expects a capsule to have a `panini$getAllState()`.
    return jni_env->CallObjectMethod(sender, mid);
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
Java_org_paninij_runtime_check_Ownership_move(JNIEnv*  jni_env,
                                              jclass,  // Ownership.class
                                              jobject  sender,
                                              jobject  sender_encapsulated,
                                              jobject, // receiver (cur. unused)
                                              jobject  ref)
{
    assert(sender != nullptr && sender_encapsulated != nullptr);

    if (ref == nullptr) {
        return;
    }

    // Notice that under the assumption of strong ownership and state
    // encapsulation, there are no data-races between these different calls to
    // `FollowReferences()`. This is because under these assumptions, the only
    // Java thread which may modify the `sender` or `ref` object graphs is the
    // Java thread which called this JNI method.

    jvmtiError err;
    bool found_illegal_move = false;
    tag_all_reachable(ref, MOVE_TAG);

    // Search for objs reachable from `sender_state` but marked with `MOVE_TAG`.
    jobject sender_state = get_all_state(jni_env, sender);
    err = jvmti_env->FollowReferences(DO_NOT_FILTER_HEAP_CALLBACKS_BY_TAG,
                                      DO_NOT_FILTER_HEAP_CALLBACKS_BY_CLASS,
                                      sender_state,
                                      &heap_searching_callbacks,
                                      &found_illegal_move);
    assert(err == JVMTI_ERROR_NONE);

    if (found_illegal_move) {
        // TODO: Consider using `GetObjectsWithTags()`
        const char* cls_name = "org/paninij/runtime/check/OwnershipMoveError";
        jclass cls = jni_env->FindClass(cls_name);
        jni_env->ThrowNew(cls, "Detected an illegal ownership move.");
    } else {
        // Otherwise, un-tag all objects reachable from `ref`
        tag_all_reachable(ref, NO_TAG);
    }
}

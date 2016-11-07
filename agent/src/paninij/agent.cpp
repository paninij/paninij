#include <iostream>
#include <jvmti.h>
#include "paninij/agent.h"
#include "paninij/ownership.h"


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
        ! set_event_callbacks(env) ||
        ! set_heap_iteration(env))
    {
        jvmtiError err = env->DisposeEnvironment();
        env = nullptr;
        return JNI_ERR;
    }

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


bool set_heap_iteration(jvmtiEnv* env) {
    // TODO: Everything!
    return true;
}


/*****************************************************************************
 * Agent Callbacks                                                           *
 *****************************************************************************/

static void JNICALL
vm_object_alloc_cb(jvmtiEnv *jvmti_env, JNIEnv* jni_env, jthread thread,
                   jobject object, jclass object_klass, jlong size) {
    std::cout << "vm_object_alloc_cb()" << std::endl;
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
 * to the `reciever` capsule. The given `clazz` is always the `Ownership` class
 * itself.
 */
JNIEXPORT void JNICALL
Java_org_paninij_runtime_check_Ownership_move(JNIEnv* env, jclass clazz,
                                              jobject sender, jobject receiver,
                                              jobject ref) {
    std::cout << "Ownership.move() was called." << std::endl;
}

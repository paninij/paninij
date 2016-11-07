#include <iostream>
#include <jni.h>

#ifndef AT_PANINIJ_AGENT_H
#define AT_PANINIJ_AGENT_H

/** The set of capabilities which the agent will need. */
const jvmtiCapabilities agent_capabilities = {
    .can_tag_objects = 1,
    .can_generate_vm_object_alloc_events = 1,
};


/** Adds all capabilities needed by the agent to the given `env`. */
bool add_capabilities(jvmtiEnv *env);


/** The kinds of events which the agent needs to be enabled. */
jvmtiEvent enabled_events[] {
    JVMTI_EVENT_VM_OBJECT_ALLOC,
};


/** Enables all events needed by the agent to be emitted via the given `env`.
 *  Events are enabled across all threads. */
bool enable_events(jvmtiEnv* env);


static void JNICALL
vm_object_alloc_cb(jvmtiEnv *jvmti_env, JNIEnv* jni_env, jthread thread,
                   jobject object, jclass object_klass, jlong size);

/** A map of event callbacks which the agent will use. */
const jvmtiEventCallbacks agent_callbacks = {
    .VMObjectAlloc = &vm_object_alloc_cb,
};


/** Configures the `env` to use the callbacks in `agent_callbacks`. */
bool set_event_callbacks(jvmtiEnv* env);


/** Configures the `env` to receive needed callbacks about GC heap iteration. */
bool set_heap_iteration(jvmtiEnv* env);  // TODO: Everything!


#endif //AT_PANINIJ_AGENT_H

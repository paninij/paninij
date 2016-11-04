#include <iostream>
#include <jvmti.h>


JNIEXPORT jint JNICALL
Agent_OnLoad(JavaVM *vm, char *options, void *reserved) {
    std::cout << "Hello, from a JVM TI Agent!" << std::endl;
    return 0;
}

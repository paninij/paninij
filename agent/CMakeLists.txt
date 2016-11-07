cmake_minimum_required(VERSION 3.6)

project(paninij-agent VERSION 0.1.1 LANGUAGES CXX)

# This cache variable is set to `NOTFOUND` by default. The user must set it.
set(PANINIJ_RUNTIME_CLASSES_PATH NOTFOUND CACHE PATH
    "Absolute path to a dir including the `org.paninij.runtime.*` class files.")
if(NOT PANINIJ_RUNTIME_CLASSES_PATH)
    message(FATAL_ERROR "PANINIJ_RUNTIME_CLASSES_PATH was not set properly.")
endif(NOT PANINIJ_RUNTIME_CLASSES_PATH)

# Use the output directory for `javah` as an include directory.
add_subdirectory(javah)
include_directories(${CMAKE_CURRENT_BINARY_DIR}/javah/include)

# Find and use JVM TI headers:
find_package(JNI REQUIRED)
include_directories(${JAVA_INCLUDE_PATH})
include_directories(${JAVA_INCLUDE_PATH2})

# Add our agent as a shared library target.
include_directories(include)
add_library(agent SHARED src/paninij/agent.cpp)
target_compile_features(agent
                        PRIVATE
                        cxx_nullptr
                        cxx_range_for)
add_dependencies(agent javah)

# Add testing support:
enable_testing()
add_subdirectory(test)

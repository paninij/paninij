cmake_minimum_required(VERSION 3.6)

project(at-paninij-agent VERSION 0.1.1 LANGUAGES CXX)

# Find and use JVM TI headers:
find_package(JNI REQUIRED)
include_directories(${JAVA_INCLUDE_PATH})
include_directories(${JAVA_INCLUDE_PATH2})

# Add our agent as a shared library target.
include_directories(include)
add_library(agent SHARED src/at-paninij/agent.cpp)
set_property(TARGET agent PROPERTY CXX_STANDARD_REQUIRED 11)

# Add testing support:
enable_testing()
add_subdirectory(test)

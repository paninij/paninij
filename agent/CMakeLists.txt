cmake_minimum_required(VERSION 3.6)

project(paninij-agent VERSION 0.1.1 LANGUAGES CXX)

set(CMAKE_CXX_STANDARD 11)
set(CMAKE_CXX_STANDARD_REQUIRED on)
#set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -Wall -pedantic-errors")

# This cache variables is set to `NOTFOUND` by default so that the user will
# set it manually at CMake configuration-time.
set(PANINIJ_LANG_JAR NOTFOUND CACHE FILEPATH
    "Absolute path to the PaniniJ lang (a.k.a. run-time) JAR file.")
if(NOT PANINIJ_LANG_JAR)
    message(FATAL_ERROR "PANINIJ_LANG_JAR was not set properly.")
endif(NOT PANINIJ_LANG_JAR)


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
#enable_testing()
#add_subdirectory(test)

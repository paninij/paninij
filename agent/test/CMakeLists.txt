set(INTEGRATION_TESTS
      it1
      it2)

find_program(JAVAC_PROGRAM javac)
find_program(JAVA_PROGRAM java)
find_program(LLDB_PROGRAM lldb)
find_program(MAKE_PROGRAM make)

foreach(TEST IN LISTS INTEGRATION_TESTS)

    set(TEST_CLASSES_DIR "${CMAKE_CURRENT_BINARY_DIR}/${TEST}")
    set(TEST_CLASS "${TEST_CLASSES_DIR}/IT.class")
    set(TEST_SOURCE_DIR "${CMAKE_CURRENT_SOURCE_DIR}/${TEST}")

    # Make a directory in the build tree to hold this test's `IT.class` file.
    file(MAKE_DIRECTORY ${TEST_CLASSES_DIR})

    # Add a command to compile `IT.java` in source tree. The resulting
    # `IT.class` file will be placed in the build tree.
    add_custom_command(
        OUTPUT ${TEST_CLASS}
        COMMAND ${JAVAC_PROGRAM} -classpath ${PANINIJ_LANG_JAR}
                                 -sourcepath ${TEST_SOURCE_DIR} -d ${TEST_CLASSES_DIR}
                                             ${TEST_SOURCE_DIR}/IT.java
    )

    # Add a target which runs `IT.class` with the @PaniniJ agent.
    add_custom_target(
        ${TEST}
        COMMAND ${JAVA_PROGRAM} -classpath "${PANINIJ_LANG_JAR}:${TEST_CLASSES_DIR}"
                                -agentpath:$<TARGET_FILE:agent> IT
        DEPENDS ${TEST_CLASS}
    )
    add_dependencies(${TEST} agent)

    # Add a target which runs `IT.class` with the @PaniniJ agent with LLDB.
    add_custom_target(
        ${TEST}_lldb
        COMMAND ${LLDB_PROGRAM} --
                ${JAVA_PROGRAM} -classpath "${PANINIJ_LANG_JAR}:${TEST_CLASSES_DIR}"
                                -agentpath:$<TARGET_FILE:agent> IT
        DEPENDS ${TEST_CLASS}
    )

    # Add a CTest which invokes CMake to run the above target ${TEST}.
    # TODO: Suppress the output from CMake. We only want Java's output.
    add_test(
        NAME ${TEST}
        COMMAND ${CMAKE_COMMAND} --build ${CMAKE_CURRENT_BINARY_DIR}
                                 --target ${TEST}
    )

endforeach()

#find_program (BASH_PROGRAM bash)
#
#if (BASH_PROGRAM)
#    add_test (it1 ${BASH_PROGRAM} ${CMAKE_CURRENT_SOURCE_DIR}/it1/it1.sh ${LIBRARY_OUTPUT_DIRECTORY}/agent)
#endif (BASH_PROGRAM)

set(INTEGRATION_TESTS it1)
find_program (BASH_PROGRAM bash)

foreach(TEST IN LISTS INTEGRATION_TESTS)
add_custom_target(${TEST}
                  COMMAND ./it.sh ${TEST} $<TARGET_FILE:agent>
                  WORKING_DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR})

add_test(NAME ${TEST}
        COMMAND ${BASH_PROGRAM} it.sh ${TEST} $<TARGET_FILE:agent>
        WORKING_DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR})
endforeach()

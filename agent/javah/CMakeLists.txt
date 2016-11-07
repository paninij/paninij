set(PANINIJ_JAVAH_INCLUDE_DIR ${CMAKE_CURRENT_BINARY_DIR}/include)
file(MAKE_DIRECTORY ${PANINIJ_JAVAH_INCLUDE_DIR})
file(MAKE_DIRECTORY ${PANINIJ_JAVAH_INCLUDE_DIR}/paninij)

find_program(JAVAH_PROGRAM javah)

set(OWNERSHIP_CLASS org.paninij.runtime.check.Ownership)
set(OWNERSHIP_H ${PANINIJ_JAVAH_INCLUDE_DIR}/paninij/ownership.h)

add_custom_command(
    OUTPUT ${OWNERSHIP_H}
    COMMAND ${JAVAH_PROGRAM} -classpath ${PANINIJ_RUNTIME_CLASSES_PATH}
                             -o ${OWNERSHIP_H} ${OWNERSHIP_CLASS}
)

add_custom_target(javah DEPENDS ${OWNERSHIP_H})

#!/usr/bin/env bash

if [ -z "$1" ]; then
    echo "Error: Expected an integration test directory, but none given."
    exit 1
else
    IT_DIR="$1"
    echo "Using integration test directory: " ${IT_DIR}
fi

if [ -z "$2" ]; then
    echo "Error: Expected a path to the agent as an argument, but none given."
    exit 1
else
    AGENT_PATH="$2"
    echo "Using agent path: " ${AGENT_PATH}
fi

# TODO: The Java `.class` files belong in the build tree, not the source tree.
javac -sourcepath "${IT_DIR}" -d "${IT_DIR}" "${IT_DIR}/IT.java"
java -classpath "${IT_DIR}" "-agentpath:${AGENT_PATH}" IT
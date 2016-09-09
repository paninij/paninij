#!/bin/bash

echo ==========
echo Executing mvnw wrapper on core:
echo mvn clean "$@"
echo ==========

cd at-paninij-core
mvn clean "$@"

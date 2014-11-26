#!/bin/bash
set -o nounset
set -o errexit
mvn clean install assembly:single
clear
java -cp target/infection-jar-with-dependencies.jar org.kedar.kai.Shell
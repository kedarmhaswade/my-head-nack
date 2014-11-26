#!/bin/bash
# Generates the configuration file: graph.conf
# First line: number of vertices
# Each subsequent line: coach-id followed by a comma-separated list of student-id's
# Example:
# 10
# 1 2,3,4
# 3 4,5
# 4 6,7,8,9
# 9 10
set -o nounset
set -o errexit

if [[ "$1" == "" ]]
then
  VERTICES=10
else
  VERTICES=$1
fi
mvn clean install assembly:single
clear
java -cp target/infection-jar-with-dependencies.jar org.kedar.kai.GraphGen ${VERTICES}
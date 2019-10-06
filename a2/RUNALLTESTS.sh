#!/bin/bash

STARTDIR="$(pwd)"
cd "$(dirname "$0")"
BASEDIR="$(pwd)"
cd "${STARTDIR}"

for filename in ./tests/passing/**/*.488;
do
  echo -e "-> Parsing "$filename '\n'
  java -jar "${BASEDIR}/dist/compiler488.jar" $filename
  echo -e ""
done

for filename in ./tests/failing/**/*.488;
do
  echo -e "-> Parsing "$filename '\n'
  java -jar "${BASEDIR}/dist/compiler488.jar" $filename
  echo -e ""
done

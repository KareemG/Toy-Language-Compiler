#!/bin/sh

# Global variable to hold the passed and failed tests per run
declare -i PASSED=0
declare -i FAILED=0

# Prerequisite: argument 1 is a main directory where all the tests are
#		argument 2 is the expected behaviour (fail or pass?). Either p or f.
# Behaviour:	Runs all of the test within given directory, and returns
#		number of failed tests and passed tests.
function runtest() {
	DIR=$1

	# Traverse through all of the sub-directories within given directory
	for subdir in $DIR/*/
	do
		echo "$subdir"
	done
}

# Prerequisite: This script must be run on the main directory of the A2 project.
#		This is where "RUNALLTESTS.sh" and "RUNCOMPILER.sh" and "tests"
#		directory should be located.

ROOT="$(pwd)"

# TODO: Uncomment once passing tests are merged
# runtest $ROOT/tests/passing

# Reset after the first run
let PASSED=0
let FAILED=0

runtest $ROOT/tests/failing

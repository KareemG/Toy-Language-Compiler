#!/bin/bash

# Global variable to hold the passed and failed tests per run
declare -i PASSED=0
declare -i FAILED=0

declare -a pARRAY
declare -a fARRAY
declare -i pARRCOUNT=0
declare -i fARRCOUNT=0

# Prerequisite: argument 1 is a main directory where all the tests are
#		argument 2 is the expected behaviour (fail or pass?). Either p or f.
# Behaviour:	Runs all of the test within given directory, and returns
#		number of failed tests and passed tests.
function runtest() {
	DIR=$1
	FLAG=$2

	# Traverse through all of the sub-directories within given directory
	for subdir in $DIR/*/
	do
		echo "Entering "$(basename $subdir)"..."

		# Traverse through all of the tests within given subdirectory
		for testcase in $subdir*
		do
			OUT="$($ROOT/RUNCOMPILER.sh $testcase \
				2> /dev/stdout | grep 'Syntax error\|Exception')"
			if [ -z "$OUT" ]
			then
				echo "PASSED: "$(basename $testcase)""
				let PASSED++
				if [ "$FLAG" == 'f' ]
				then
					fARRAY[$fARRCOUNT]="$(basename $testcase)"
					let fARRCOUNT+=1
				fi
			else
				echo "FAILED: "$(basename $testcase)""
				let FAILED++
				if [ "$FLAG" == 'p' ]
				then
					pARRAY[$pARRCOUNT]="$(basename $testcase)"
					let pARRCOUNT+=1
				fi
			fi
		done
		echo ""
	done
}

# Prerequisite: This script must be run on the main directory of the A2 project.
#		This is where "RUNALLTESTS.sh" and "RUNCOMPILER.sh" and "tests"
#		directory should be located.

ROOT="$(pwd)"

echo "===== RUNNING PASSING TESTS ====="
runtest $ROOT/tests/passing 'p'

let pPASSED=$PASSED
let pFAILED=$FAILED
let pTOTAL=$PASSED+$FAILED

# Reset after the first run
let PASSED=0
let FAILED=0

echo "===== RUNNING FAILING TESTS ====="
runtest $ROOT/tests/failing 'f'

let TOTAL=$PASSED+$FAILED

echo "===== SUMMARY ====="
echo -e "Passing tests:"
echo -e "\tPassed tests: $pPASSED"
echo -e "\tFailed tests: $pFAILED"
echo -e "\tTotal tests: $pTOTAL"
echo -e "\tFailed tests expected to pass: "
echo -e "\t\t${pARRAY[@]}"
echo ""
echo -e "Failing tests:"
echo -e "\tPassed tests: $PASSED"
echo -e "\tFailed tests: $FAILED"
echo -e "\tTotal tests: $TOTAL"
echo -e "\tPassed tests expected to fail: "
echo -e "\t\t${fARRAY[@]}"

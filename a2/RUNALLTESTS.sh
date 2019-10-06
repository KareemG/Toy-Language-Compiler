#!/bin/sh

# Prerequisite: argument 1 is a main directory where all the tests are
# Behaviour:	Runs all of the test within given directory, and returns
#		number of failed tests and passed tests.
function runtest() {
	DIR=$1
	declare -i PASSED=0
}

ROOT="$(PWD)"

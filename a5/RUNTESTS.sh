#!/bin/bash

STATUS=1

function runpassing()
{
	path="./testing/pass"

	for source in $path/*.488
	do
		test_name="$(basename ${source})"
		test_name="${test_name%.488}"
		output="$(./RUNCOMPILER.sh "${path}/${test_name}.488" 2> /dev/null | grep "Result")"

		if [ "$output" ]
		then
			if [ "$(grep "$output" "${path}/${test_name}.out")" ]
			then
				echo -n "PASS: "
			else
				STATUS=0
				echo -n "FAIL: "
			fi
		else
			STATUS=0
			echo -n "FAIL: "
		fi
		echo "${test_name}.488"
	done
}

function runcodegen()
{
	path="./testing/fail/codegen"
	echo "CODEGEN:"

	for source in $path/*.488
	do
		test_name="$(basename ${source})"
		test_name="${test_name%.488}"
		output="$(./RUNCOMPILER.sh "${path}/${test_name}.488" 2>&1 | grep "Execution Error")"

		if [ "$output" ]
		then
			echo -n "PASS: "
		else
			STATUS=0
			echo -n "FAIL: "
		fi
		echo "${test_name}.488"
	done

}

function runsemantics()
{
	path="./testing/fail/semantics"
	echo "SEMANTICS:"

	for source in $path/*.488
	do
		test_name="$(basename ${source})"
		test_name="${test_name%.488}"
		output="$(./RUNCOMPILER.sh "${path}/${test_name}.488" 2>&1 | grep "Exception during Semantic Analysis")"

		if [ "$output" ]
		then
			echo -n "PASS: "
		else
			STATUS=0
			echo -n "FAIL: "
		fi
		echo "${test_name}.488"
	done
}

function runsyntax()
{
	path="./testing/fail/syntax"
	echo "SYNTAX:"

	for source in $path/*.488
	do
		test_name="$(basename ${source})"
		test_name="${test_name%.488}"
		output="$(./RUNCOMPILER.sh "${path}/${test_name}.488" 2>&1 | grep 'Syntax error\|Exception')"

		if [ "$output" ]
		then
			echo -n "PASS: "
		else
			STATUS=0
			echo -n "FAIL: "
		fi
		echo "${test_name}.488"
	done
}

echo "=================="
echo "PASSING TEST CASES"
echo "=================="

runpassing
echo ""

echo "=================="
echo "FAILING TEST CASES"
echo "=================="

runcodegen
echo ""
runsemantics
echo ""
runsyntax

printf "\n\nRESULT: "
if [ "$STATUS" -eq 1 ]
then
	echo "PASS"
else
	echo "FAIL"
fi

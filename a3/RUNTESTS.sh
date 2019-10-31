#!/bin/bash

function ast_test # test_name, test_source, expected_output
{
    output="$(java -jar ./dist/compiler488.jar -D b $2 2> /dev/null | tr -s ' \n')"
    output="${output:18+${#2}:${#output}-54-${#2}}"

    expected="$(cat $3)"

    difference="$(diff <(echo "$output" ) <(echo "$expected"))"

    echo -n "$1: "
    if [ "${#difference}" -gt 0 ]; then
        echo "FAIL"
    else
        echo "PASS"
    fi
}

function semantic_catch # test_name, test_source
{
    result="$(java -jar ./dist/compiler488.jar -D b $2 2>&1 | grep 'Exception during Semantic Analysis')"
    echo -n "$1: "
    if [ ${#result} -gt 0 ]; then
        echo "SUCCESS"
    else
        echo "FAIL"
    fi
}

function ast_tests
{
    path="./test/passing/ast"

    printf "ast tests:\n"

    for source in $path/*.488
    do
        test_name="$(basename ${source})"
        test_name="${test_name%.488}"

        ast_test "${test_name}" "${path}/${test_name}.488" "${path}/${test_name}.out"
    done
}

function semantic_tests
{
    path="./test/failing"

    printf "\n\nsemantic tests:\n"

    for source in $path/*.488
    do
        test_name="$(basename ${source})"
        test_name="${test_name%.488}"

        semantic_catch "${test_name}" "${source}"
    done
}

ast_tests
semantic_tests

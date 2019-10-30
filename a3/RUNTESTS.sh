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

printf "ast tests:\n"
ast_test "array_1D" "./test/passing/ast/array_1D.488" "./test/passing/ast/array_1D.out"
ast_test "array_2D" "./test/passing/ast/array_2D.488" "./test/passing/ast/array_2D.out"
ast_test "exit" "./test/passing/ast/exit.488" "./test/passing/ast/exit.out"
ast_test "expressions" "./test/passing/ast/expressions.488" "./test/passing/ast/expressions.out"
ast_test "functions" "./test/passing/ast/functions.488" "./test/passing/ast/functions.out"
ast_test "if_stmt" "./test/passing/ast/if_stmt.488" "./test/passing/ast/if_stmt.out"
ast_test "loops" "./test/passing/ast/loops.488" "./test/passing/ast/loops.out"
ast_test "procedures" "./test/passing/ast/procedures.488" "./test/passing/ast/procedures.out"
ast_test "return" "./test/passing/ast/return.488" "./test/passing/ast/return.out"
ast_test "scope_statements_only" "./test/passing/ast/scope_statements_only.488" "./test/passing/ast/scope_statements_only.out"

printf "\n\nsemantic tests:\n"
semantic_catch "add_bools" "./test/failing/add_bools.488"
semantic_catch "add_int_and_bool" "./test/failing/add_int_and_bool.488"
semantic_catch "array_type_mismatch" "./test/failing/array_type_mismatch.488"
semantic_catch "assign_bool_to_int" "./test/failing/assign_bool_to_int.488"
semantic_catch "assign_int_to_bool" "./test/failing/assign_int_to_bool.488"
semantic_catch "assign_undeclared_var" "./test/failing/assign_undeclared_var.488"
semantic_catch "bad_array" "./test/failing/bad_array.488"
semantic_catch "call_undeclared_function" "./test/failing/call_undeclared_function.488"
semantic_catch "call_undeclared_procedure" "./test/failing/call_undeclared_procedure.488"
semantic_catch "call_variable" "./test/failing/call_variable.488"
semantic_catch "different_parameter_type" "./test/failing/different_parameter_type.488"
semantic_catch "duplicate_definition" "./test/failing/duplicate_definition.488"
semantic_catch "duplicate_function" "./test/failing/duplicate_function.488"
semantic_catch "duplicate_variable" "./test/failing/duplicate_variable.488"
semantic_catch "excessive_parameters" "./test/failing/excessive_parameters.488"
semantic_catch "exit_invalid" "./test/failing/exit_invalid.488"
semantic_catch "exit_zero" "./test/failing/exit_zero.488"
semantic_catch "external_exit" "./test/failing/external_exit.488"
semantic_catch "external_return" "./test/failing/external_return.488"
semantic_catch "missing_parameters" "./test/failing/missing_parameters.488"
semantic_catch "no_parameters" "./test/failing/no_parameters.488"
semantic_catch "no_return_stmt" "./test/failing/no_return_stmt.488"
semantic_catch "out_of_scope_function" "./test/failing/out_of_scope_function.488"
semantic_catch "out_of_scope_variable" "./test/failing/out_of_scope_variable.488"
semantic_catch "return_from_func" "./test/failing/return_from_func.488"
semantic_catch "return_with_proc" "./test/failing/return_with_proc.488"
semantic_catch "sub_int_and_bool_expr" "./test/failing/sub_int_and_bool_expr.488"
semantic_catch "var_same_name_as_function" "./test/failing/var_same_name_as_function.488"
semantic_catch "var_same_name_as_param" "./test/failing/var_same_name_as_param.488"

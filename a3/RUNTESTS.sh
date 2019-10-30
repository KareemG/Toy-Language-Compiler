#!/bin/bash

function ast_test # test_name, test_source, expected_output
{
    output="$(java -jar ./dist/compiler488.jar -D a $2 2> /dev/null | tr -s ' \n')"
    output="${output:33+${#1}:${#output}-69-${#1}}"

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
    result="$(java -jar ./dist/compiler488.jar -D a $2 2>&1 | grep 'Exception during Semantic Analysis')"
    echo -n "$1: "
    if [ ${#result} -gt 0 ]; then
        echo "SUCCESS"
    else
        echo "FAIL"
    fi
}

printf "ast tests:\n"
ast_test "array_1D" "./test/ast/array_1D.488" "./test/ast/array_1D.out"
ast_test "array_2D" "./test/ast/array_2D.488" "./test/ast/array_2D.out"
ast_test "exit" "./test/ast/exit.488" "./test/ast/exit.out"
ast_test "expressions" "./test/ast/expressions.488" "./test/ast/expressions.out"
ast_test "functions" "./test/ast/functions.488" "./test/ast/functions.out"
ast_test "if_stmt" "./test/ast/if_stmt.488" "./test/ast/if_stmt.out"
ast_test "loops" "./test/ast/loops.488" "./test/ast/loops.out"
ast_test "procedures" "./test/ast/procedures.488" "./test/ast/procedures.out"
ast_test "return" "./test/ast/return.488" "./test/ast/return.out"
ast_test "scope_statements_only" "./test/ast/scope_statements_only.488" "./test/ast/scope_statements_only.out"

printf "\n\nsemantic tests:\n"
semantic_catch "add_bools" "add_bools.488"
semantic_catch "add_int_and_bool" "add_int_and_bool.488"
semantic_catch "array_type_mismatch" "array_type_mismatch.488"
semantic_catch "assign_bool_to_int" "assign_bool_to_int.488"
semantic_catch "assign_int_to_bool" "assign_int_to_bool.488"
semantic_catch "assign_undeclared_var" "assign_undeclared_var.488"
semantic_catch "bad_array" "bad_array.488"
semantic_catch "call_undeclared_function" "call_undeclared_function.488"
semantic_catch "call_undeclared_procedure" "call_undeclared_procedure.488"
semantic_catch "call_variable" "call_variable.488"
semantic_catch "different_parameter_type" "different_parameter_type.488"
semantic_catch "duplicate_definition" "duplicate_definition.488"
semantic_catch "duplicate_function" "duplicate_function.488"
semantic_catch "duplicate_variable" "duplicate_variable.488"
semantic_catch "excessive_parameters" "excessive_parameters.488"
semantic_catch "exit_invalid" "exit_invalid.488"
semantic_catch "exit_zero" "exit_zero.488"
semantic_catch "external_exit" "external_exit.488"
semantic_catch "external_return" "external_return.488"
semantic_catch "missing_parameters" "missing_parameters.488"
semantic_catch "no_parameters" "no_parameters.488"
semantic_catch "no_return_stmt" "no_return_stmt.488"
semantic_catch "out_of_scope_function" "out_of_scope_function.488"
semantic_catch "out_of_scope_variable" "out_of_scope_variable.488"
semantic_catch "return_from_func" "return_from_func.488"
semantic_catch "return_with_proc" "return_with_proc.488"
semantic_catch "sub_int_and_bool_expr" "sub_int_and_bool_expr.488"
semantic_catch "var_same_name_as_function" "var_same_name_as_function.488"
semantic_catch "var_same_name_as_param" "var_same_name_as_param.488"

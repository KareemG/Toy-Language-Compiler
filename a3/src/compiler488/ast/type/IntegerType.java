package compiler488.ast.type;

import compiler488.semantics.AST_Visitor;

/**
 * Used to declare objects that yield integers.
 */
public class IntegerType extends Type {
    public String toString() {
        return "integer";
    }

    @Override
    public void accept(AST_Visitor visitor) {
        visitor.visit(this);
    }
}

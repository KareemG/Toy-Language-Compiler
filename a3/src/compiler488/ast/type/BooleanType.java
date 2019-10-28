package compiler488.ast.type;

import compiler488.semantics.AST_Visitor;

/**
 * The type of things that may be true or false.
 */
public class BooleanType extends Type {
    @Override
    public String toString() {
        return "boolean";
    }

    @Override
    public void accept(AST_Visitor visitor) {
        visitor.visit(this);
    }
}

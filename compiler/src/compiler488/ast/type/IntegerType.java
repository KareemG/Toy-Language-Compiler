package compiler488.ast.type;

import compiler488.semantics.ASTVisitor;

/**
 * Used to declare objects that yield integers.
 */
public class IntegerType extends Type {
    public String toString() {
        return "integer";
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visit(this);
    }
}

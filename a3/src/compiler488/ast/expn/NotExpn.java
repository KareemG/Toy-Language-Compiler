package compiler488.ast.expn;

import compiler488.semantics.AST_Visitor;

/**
 * Represents the boolean negation of an expression.
 */
public class NotExpn extends UnaryExpn {
    public NotExpn(Expn operand) {
        super(UnaryExpn.OP_NOT, operand);
    }

    @Override
    public void accept(AST_Visitor visitor) {
    }
}

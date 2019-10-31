package compiler488.ast.expn;

import compiler488.semantics.ASTVisitor;

/**
 * Represents the boolean negation of an expression.
 */
public class NotExpn extends UnaryExpn {
    public NotExpn(Expn operand) {
        super(UnaryExpn.OP_NOT, operand);
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visitEnter(this);
        this.getOperand().accept(visitor);
        visitor.visitLeave(this);
    }
}

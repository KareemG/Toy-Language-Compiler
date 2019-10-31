package compiler488.ast.expn;

import compiler488.semantics.ASTVisitor;

/**
 * Represents negation of an integer expression
 */
public class UnaryMinusExpn extends UnaryExpn {
    public UnaryMinusExpn(Expn operand) {
        super(UnaryExpn.OP_MINUS, operand);
    }

    @Override
    public void accept(ASTVisitor visitor) {
        visitor.visitEnter(this);
        this.getOperand().accept(visitor);
        visitor.visitLeave(this);
    }
}

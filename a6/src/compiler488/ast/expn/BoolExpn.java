package compiler488.ast.expn;

import compiler488.semantics.ASTVisitor;

/**
 * Place holder for all binary expression where both operands must be boolean
 * expressions.
 */
public class BoolExpn extends BinaryExpn {
    public final static String OP_OR 	= "or";
    public final static String OP_AND	= "and";

    public BoolExpn(String opSymbol, Expn left, Expn right) {
        super(opSymbol, left, right);

        assert ((opSymbol == OP_OR) ||
                (opSymbol == OP_AND));
    }

    @Override
    public void accept(ASTVisitor visitor) {
        this.left.accept(visitor);
        visitor.visitEnter(this);
        this.right.accept(visitor);
        visitor.visitLeave(this);
    }

}

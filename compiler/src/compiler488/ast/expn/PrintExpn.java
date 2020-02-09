package compiler488.ast.expn;

import compiler488.ast.BaseAST;
import compiler488.ast.Printable;
import compiler488.ast.PrettyPrinter;
import compiler488.semantics.ASTVisitor;

/*
 * Represents an expression which will be written
*/
public class PrintExpn extends BaseAST implements Printable
{
    private Expn expn;

    public PrintExpn(Expn e)
    {
        this.expn = e;
    }

    public Expn getExpn()
    {
        return this.expn;
    }

    @Override
    public void prettyPrint(PrettyPrinter p)
    {
        this.expn.prettyPrint(p);
    }

    @Override
    public void accept(ASTVisitor visitor)
    {
        this.expn.accept(visitor);
        visitor.visit(this);
    }
}

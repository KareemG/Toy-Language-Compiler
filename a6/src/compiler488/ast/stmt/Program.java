package compiler488.ast.stmt;

import compiler488.semantics.*;

/**
 * Placeholder for the scope that is the entire program
 */
public class Program extends Scope
{
    public Program(Scope scp)
    {
        super(scp.getDeclarations(), scp.getStatements());
    }

    // accept method simply calls scope method
    // here in case we want to add any special functionality to "Program" later on
    @Override
    public void accept(ASTVisitor visitor)
    {
        visitor.visitEnter(this);
        super.accept(visitor);
        visitor.visitLeave(this);
    }
}

package compiler488.semantics;

import compiler488.ast.*;
import compiler488.ast.decl.*;
import compiler488.ast.stmt.*;

public interface AST_Visitor
{
    public void visitEnter(BaseAST node);
    public void visit(BaseAST node);
    public void visitLeave(BaseAST node);

    public void visitEnter(Program node);
    public void visit(Program node);
    public void visitLeave(Program node);

    public void visitEnter(Scope node);
    public void visit(Scope node);
    public void visitLeave(Scope node);

    public void visitEnter(RoutineDecl node);
    public void visitLeave(RoutineDecl node);

    public void visitEnter(LoopingStmt node);
    public void visitLeave(LoopingStmt node);

    public void visit(ExitStmt node);
    
    public void visit(ReturnStmt node);
}

package compiler488.semantics;

import compiler488.ast.*;
import compiler488.ast.decl.*;
import compiler488.ast.stmt.*;

public interface AST_Visitor
{
    public Semantics analyzer = new Semantics();

    // ===== NON LEAF NODES ===== //
    public void visitEnter(Program node);
    public void visitLeave(Program node);

    public void visitEnter(Scope node);
    public void visitLeave(Scope node);

    public void visitEnter(RoutineDecl node);
    public void visitLeave(RoutineDecl node);

    public void visitEnter(LoopingStmt node);
    public void visitLeave(LoopingStmt node);

    // ===== LEAF NODES ===== //
    public void visit(ExitStmt node);
    public void visit(ReturnStmt node);

    public static class Default implements AST_Visitor {
        // ===== DEFAULT ACTIONS ===== //
        void defaultVisit(BaseAST node) {}
        void defaultVisitEnter(BaseAST node) {
            defaultVisit(node);
        }
        void defaultVisitLeave(BaseAST node) {
            defaultVisit(node);
        }
        void defaultVisitForLeaf(BaseAST node) {
            defaultVisit(node);
        }

        // ===== NON LEAF NODES ===== //
        public void visitEnter(Program prog) {
            defaultVisitEnter(prog);
        }
        public void visitLeave(Program prog) {
            defaultVisitLeave(prog);
        }

        public void visitEnter(Scope scope) {
            defaultVisitEnter(scope);
        }
        public void visitLeave(Scope scope) {
            defaultVisitLeave(scope);
        }

        public void visitEnter(RoutineDecl routine) {
            defaultVisitEnter(routine);
        }
        public void visitLeave(RoutineDecl routine) {
            defaultVisitLeave(routine);
        }

        // ===== LEAF NODES ===== //
        public void visit(ExitStmt exitStmt) {
            defaultVisitForLeaf(exitStmt);
        }
        public void visit(ReturnStmt retStmt) {
            defaultVisitForLeaf(retStmt);
        }
    }
}

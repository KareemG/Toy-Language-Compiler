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

    public void visitEnter(MultiDeclarations decl);
    public void visitLeave(MultiDeclarations decl);

    public void visitEnter(AssignStmt assign);
    public void visitLeave(AssignStmt assign);

    public void visitEnter(ProcedureCallStmt procStmt);
    public void visitLeave(ProcedureCallStmt procStmt);

    public void visitEnter(LoopingStmt node);
    public void visitLeave(LoopingStmt node);

    public void visitEnter(IfStmt node);
    public void visitLeave(IfStmt node);

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
        public void visitEnter(MultiDeclarations decls) {
            defaultVisitEnter(decls);
        }
        public void visitLeave(MultiDeclarations decls) {
            defaultVisitLeave(decls);
        }

        public void visitEnter(AssignStmt assign) {
            defaultVisitEnter(assign);
        }
        public void visitLeave(AssignStmt assign) {
            defaultVisitLeave(assign);
        }
        public void visitEnter(ProcedureCallStmt procStmt) {
            defaultVisitEnter(procStmt);
        }
        public void visitLeave(ProcedureCallStmt procStmt) {
            defaultVisitLeave(procStmt);
        }
        public void visitEnter(LoopingStmt loopStmt) {
            defaultVisitEnter(loopStmt);
        }
        public void visitLeave(LoopingStmt loopStmt) {
            defaultVisitLeave(loopStmt);
        }
        public void visitEnter(IfStmt ifStmt) {
            defaultVisitEnter(ifStmt);
        }
        public void visitLeave(IfStmt ifStmt) {
            defaultVisitLeave(ifStmt);
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

package compiler488.semantics;

import compiler488.ast.*;
import compiler488.ast.decl.*;
import compiler488.ast.stmt.*;
import compiler488.ast.type.BooleanType;
import compiler488.ast.type.IntegerType;

public interface AST_Visitor
{
    public Semantics analyzer = new Semantics();

    // ===== NON LEAF NODES ===== //
    public void visitEnter(Program node);
    public void visitLeave(Program node);

    public void visitEnter(Scope routine);
    public void visitLeave(Scope routine);

    public void visitEnter(RoutineDecl routine);
    public void visitLeave(RoutineDecl routine);
    public void visitEnter(Declaration decl);
    public void visitLeave(Declaration decl);
    public void visitEnter(MultiDeclarations decl);
    public void visitLeave(MultiDeclarations decl);
    public void visitEnter(ScalarDecl decl);
    public void visitLeave(ScalarDecl decl);

    public void visitEnter(AssignStmt assign);
    public void visitLeave(AssignStmt assign);
    public void visitEnter(ProcedureCallStmt procStmt);
    public void visitLeave(ProcedureCallStmt procStmt);
    public void visitEnter(LoopingStmt loopStmt);
    public void visitLeave(LoopingStmt loopStmt);
    public void visitEnter(IfStmt ifStmt);
    public void visitLeave(IfStmt ifStmt);

    // ===== LEAF NODES ===== //
    public void visit(DeclarationPart declPart);
    public void visit(ArrayDeclPart arrPart);
    public void visit(ScalarDeclPart scaPart);
    public void visit(ExitStmt node);
    public void visit(ReturnStmt node);
    public void visit(BooleanType boolType);
    public void visit(IntegerType intType);

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

        public void visitEnter(Declaration decl) {
            defaultVisitEnter(decl);
        }
        public void visitLeave(Declaration decl) {
            defaultVisitLeave(decl);
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
        public void visitEnter(ScalarDecl decl) {
            defaultVisitEnter(decl);
        }
        public void visitLeave(ScalarDecl decl) {
            defaultVisitLeave(decl);
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
        public void visit(DeclarationPart declPart) {
            defaultVisitForLeaf(declPart);
        }
        public void visit(ArrayDeclPart arrPart) {
            defaultVisitForLeaf(arrPart);
        }
        public void visit(ScalarDeclPart scaPart) {
            defaultVisitForLeaf(scaPart);
        }
        public void visit(ScalarDecl scalar) {
            defaultVisitForLeaf(scalar);
        }
        public void visit(ExitStmt exitStmt) {
            defaultVisitForLeaf(exitStmt);
        }
        public void visit(ReturnStmt retStmt) {
            defaultVisitForLeaf(retStmt);
        }
        public void visit(BooleanType boolType) {
            defaultVisitForLeaf(boolType);
        }
        public void visit(IntegerType intType) {
            defaultVisitForLeaf(intType);
        }
    }
}

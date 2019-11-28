package compiler488.semantics;

import compiler488.ast.*;
import compiler488.ast.decl.*;
import compiler488.ast.expn.*;
import compiler488.ast.stmt.*;
import compiler488.ast.type.*;

public interface ASTVisitor
{
    public Semantics analyzer = new Semantics();

    // ===== NON LEAF NODES ===== //
    public void visitEnter(Program node);
    public void visitLeave(Program node);

    public void visitEnter(Scope routine);
    public void visitLeave(Scope routine);

    public void visitEnter(RoutineDecl routine);
    public void visitLeave(RoutineDecl routine);
    public void visitEnter(MultiDeclarations decl);
    public void visitLeave(MultiDeclarations decl);
    public void visitEnter(ScalarDecl decl);
    public void visitLeave(ScalarDecl decl);

    public void visitEnter(AssignStmt assign);
    public void visitLeave(AssignStmt assign);
    public void visitEnter(ProcedureCallStmt procStmt);
    public void visitLeave(ProcedureCallStmt procStmt);
    public void visitEnter(WhileDoStmt whileStmt);
    public void visitLeave(WhileDoStmt whileStmt);
    public void visitEnter(RepeatUntilStmt repeatStmt);
    public void visitLeave(RepeatUntilStmt repeatStmt);
    public void visitEnter(ReadStmt readStmt);
    public void visitLeave(ReadStmt readStmt);
    public void visitEnter(ScopeStmt scopeStmt);
    public void visitLeave(ScopeStmt scopeStmt);
    public void visitEnter(IfStmt ifStmt);
    public void visitLeave(IfStmt ifStmt);
    public void visitEnter(WriteStmt writeStmt);
    public void visitLeave(WriteStmt writeStmt);

    public void visitEnter(ArithExpn arith);
    public void visitLeave(ArithExpn arith);
    public void visitEnter(BoolExpn boolExpn);
    public void visitLeave(BoolExpn boolExpn);
    public void visitEnter(CompareExpn compExpn);
    public void visitLeave(CompareExpn compExpn);
    public void visitEnter(ConditionalExpn condExpn);
    public void visitLeave(ConditionalExpn condExpn);
    public void visitEnter(EqualsExpn equalExpn);
    public void visitLeave(EqualsExpn equalExpn);
    public void visitEnter(FunctionCallExpn funcExpn);
    public void visitLeave(FunctionCallExpn funcExpn);
    public void visitEnter(NotExpn notExpn);
    public void visitLeave(NotExpn notExpn);
    public void visitEnter(SubsExpn subExpn);
    public void visitLeave(SubsExpn subExpn);
    public void visitEnter(UnaryMinusExpn minusExpn);
    public void visitLeave(UnaryMinusExpn minusExpn);

    public void visit(IfStmt stmt);

    // ===== LEAF NODES ===== //
    public void visit(ArrayDeclPart arrPart);
    public void visit(ScalarDeclPart scaPart);
    public void visit(ExitStmt node);
    public void visit(ReturnStmt node);
    public void visit(BooleanType boolType);
    public void visit(IntegerType intType);
    public void visit(IdentExpn ident);
    public void visit(BoolConstExpn boolExpn);
    public void visit(IntConstExpn intExpn);
    public void visit(PrintExpn printExpn);
    public void visit(SkipConstExpn skipExpn);
    public void visit(TextConstExpn textExpn);

    public static class Default implements ASTVisitor {
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
        public void visitEnter(WhileDoStmt whileStmt) {
            defaultVisitEnter(whileStmt);
        }
        public void visitLeave(WhileDoStmt whileStmt) {
            defaultVisitLeave(whileStmt);
        }
        public void visitEnter(RepeatUntilStmt repeatStmt) {
            defaultVisitEnter(repeatStmt);
        }
        public void visitLeave(RepeatUntilStmt repeatStmt) {
            defaultVisitLeave(repeatStmt);
        }
        public void visitEnter(ReadStmt readStmt) {
            defaultVisitEnter(readStmt);
        }
        public void visitLeave(ReadStmt readStmt) {
            defaultVisitLeave(readStmt);
        }
        public void visitEnter(ScopeStmt scopeStmt) {
            defaultVisitEnter(scopeStmt);
        }
        public void visitLeave(ScopeStmt scopeStmt) {
            defaultVisitLeave(scopeStmt);
        }
        public void visitEnter(IfStmt ifStmt) {
            defaultVisitEnter(ifStmt);
        }
        public void visitLeave(IfStmt ifStmt) {
            defaultVisitLeave(ifStmt);
        }
	public void visitEnter(WriteStmt writeStmt) {
		defaultVisit(writeStmt);
	}
	public void visitLeave(WriteStmt writeStmt) {
		defaultVisit(writeStmt);
	}

        public void visitEnter(ArithExpn arith) {
            defaultVisitEnter(arith);
        }
        public void visitLeave(ArithExpn arith) {
            defaultVisitLeave(arith);
        }
        public void visitEnter(BoolExpn boolExpn) {
            defaultVisitEnter(boolExpn);
        }
        public void visitLeave(BoolExpn boolExpn) {
            defaultVisitLeave(boolExpn);
        }
        public void visitEnter(CompareExpn compExpn) {
            defaultVisitEnter(compExpn);
        }
        public void visitLeave(CompareExpn compExpn) {
            defaultVisitLeave(compExpn);
        }
        public void visitEnter(ConditionalExpn condExpn) {
            defaultVisitEnter(condExpn);
        }
        public void visitLeave(ConditionalExpn condExpn) {
            defaultVisitLeave(condExpn);
        }
        public void visitEnter(EqualsExpn equalExpn) {
            defaultVisitEnter(equalExpn);
        }
        public void visitLeave(EqualsExpn equalExpn) {
            defaultVisitLeave(equalExpn);
        }
        public void visitEnter(FunctionCallExpn funcExpn) {
            defaultVisitEnter(funcExpn);
        }
        public void visitLeave(FunctionCallExpn funcExpn) {
            defaultVisitLeave(funcExpn);
        }
        public void visitEnter(NotExpn notExpn) {
            defaultVisitEnter(notExpn);
        }
        public void visitLeave(NotExpn notExpn) {
            defaultVisitLeave(notExpn);
        }
        public void visitEnter(SubsExpn subExpn) {
            defaultVisitEnter(subExpn);
        }
        public void visitLeave(SubsExpn subExpn) {
            defaultVisitLeave(subExpn);
        }
        public void visitEnter(UnaryMinusExpn minusExpn) {
            defaultVisitEnter(minusExpn);
        }
        public void visitLeave(UnaryMinusExpn minusExpn) {
            defaultVisitLeave(minusExpn);
        }

        public void visit(IfStmt stmt) {
            defaultVisit(stmt);
        }

        // ===== LEAF NODES ===== //
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
        public void visit(IdentExpn ident) {
            defaultVisitForLeaf(ident);
        }
        public void visit(BoolConstExpn boolExpn) {
            defaultVisitForLeaf(boolExpn);
        }
        public void visit(IntConstExpn intExpn) {
            defaultVisitForLeaf(intExpn);
        }
        public void visit(PrintExpn printExpn) {
            defaultVisitForLeaf(printExpn);
        }
        public void visit(SkipConstExpn skipExpn) {
            defaultVisitForLeaf(skipExpn);
        }
        public void visit(TextConstExpn textExpn) {
            defaultVisitForLeaf(textExpn);
        }
    }
}

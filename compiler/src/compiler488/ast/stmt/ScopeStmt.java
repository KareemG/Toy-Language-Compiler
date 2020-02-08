package compiler488.ast.stmt;

import compiler488.ast.PrettyPrinter;

import compiler488.semantics.*;

/**
 * Represents an ordinary scope statement.
 * This should not be linked to procedure/function/etc...
 */
public class ScopeStmt extends Stmt {
        protected Scope scope;

        public ScopeStmt(Scope scope) {
                this.scope = scope;
        }

        public Scope getScope() {
                return this.scope;
        }

        @Override
        public void accept(ASTVisitor visitor) {
                visitor.visitEnter(this);
                this.scope.accept(visitor);
                visitor.visitLeave(this);
        }

        @Override
	public void prettyPrint(PrettyPrinter p) {
                this.scope.prettyPrint(p);
        }
}
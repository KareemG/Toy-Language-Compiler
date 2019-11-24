package compiler488.ast.stmt;

import java.util.ListIterator;

import compiler488.ast.ASTList;
import compiler488.ast.PrettyPrinter;
import compiler488.ast.decl.Declaration;

import compiler488.semantics.*;

/**
 * Represents the declarations and statements of a scope construct.
 */
public class Scope extends Stmt {
	/** Body of the scope, optional declarations, optional statements */
	protected ASTList<Declaration> declarations;
	protected ASTList<Stmt> statements;

	public Scope() {
		declarations = null;
		statements = null;
	}

	public Scope(ASTList<Stmt> stmts)
	{
		declarations = null;
		statements = stmts;
	}

	public Scope(ASTList<Declaration> decls, ASTList<Stmt> stmts)
	{
		declarations = decls;
		statements = stmts;
	}

	public void setDeclarations(ASTList<Declaration> declarations) {
		this.declarations = declarations;
	}

	public ASTList<Declaration> getDeclarations() {
		return declarations;
	}

	public void setStatements(ASTList<Stmt> statements) {
		this.statements = statements;
	}

	public ASTList<Stmt> getStatements() {
		return statements;
	}

	@Override
	public void prettyPrint(PrettyPrinter p) {
		p.println(" { ");
		if (declarations != null && declarations.size() > 0) {
			declarations.prettyPrintBlock(p);
		}
		if (statements != null && statements.size() > 0) {
			statements.prettyPrintBlock(p);
		}
		p.print(" } ");
	}

	@Override
	public void accept(ASTVisitor visitor) {
		if (declarations != null && declarations.size() > 0) {
			ListIterator<Declaration> decl_it = declarations.listIterator();
			while (decl_it.hasNext()) {
				decl_it.next().accept(visitor);
			}
		}

		visitor.visitEnter(this);

		if (statements != null && statements.size() > 0) {
			ListIterator<Stmt> stmt_it = statements.listIterator();
			while (stmt_it.hasNext()) {
				stmt_it.next().accept(visitor);
			}
		}

		visitor.visitLeave(this);
	}
}

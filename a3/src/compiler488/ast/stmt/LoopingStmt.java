package compiler488.ast.stmt;

import java.util.ListIterator;

import compiler488.ast.ASTList;
import compiler488.ast.expn.Expn;

import compiler488.semantics.AST_Visitor;

/**
 * Represents the common parts of loops.
 */
public abstract class LoopingStmt extends Stmt {
	/** The control expression for the looping construct (if any.) */
	protected Expn expn = null;

	/** The body of the looping construct. */
	protected ASTList<Stmt> body;

	public LoopingStmt(Expn expn, ASTList<Stmt> body) {
		super();

		this.expn = expn;
		this.body = body;
	}

	public LoopingStmt(ASTList<Stmt> body) {
		this(null, body);
	}

	public Expn getExpn() {
		return expn;
	}

	public ASTList<Stmt> getBody() {
		return body;
	}

	public void accept(AST_Visitor visitor)
	{
		if (this.expn != null) {
			this.expn.accept(visitor);
		}

		visitor.visitEnter(this);
		
		ListIterator<Stmt> stmt_it = body.listIterator();
		while(stmt_it.hasNext())
		{
			stmt_it.next().accept(visitor);
		}

		visitor.visitLeave(this);
	}
}

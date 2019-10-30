package compiler488.ast.stmt;

import compiler488.ast.expn.*;
import compiler488.ast.PrettyPrinter;

import compiler488.semantics.AST_Visitor;

/**
 * Represents the command to exit from a loop.
 */
public class ExitStmt extends Stmt {
	/** Condition for 'exit when'. */
	private Expn expn = null;

	/** Number of levels to exit. */
	private Integer level = -1;

	public ExitStmt()
	{
	}

	public ExitStmt(int i)
	{
		level = i;
	}

	public ExitStmt(Expn e)
	{
		expn = e;
	}

	public ExitStmt(Expn e, int i)
	{
		expn = e;
		level = i;
	}

	/**
	 * Returns the string <b>"exit"</b> or <b>"exit when e"</b>" or
	 * <b>"exit"</b> level or <b>"exit"</b> level when e
	 */
	@Override
	public String toString() {
		String stmt = "exit";

		if (level >= 0) {
			stmt = " " + stmt + level;
		}

		if (expn != null) {
			stmt = " " + stmt + "when " + expn + " ";
		}

		return stmt;
	}

	@Override
	public void prettyPrint(PrettyPrinter p)
	{
		p.print("exit");

		if(level >= 0) {
			p.print(" " + level);
		}

		if(expn != null) {
			p.print(" when ");
			expn.prettyPrint(p);
		}
	}

	public Expn getExpn() {
		return expn;
	}

	public void setExpn(Expn expn) {
		this.expn = expn;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	@Override
	public void accept(AST_Visitor visitor)
	{
		if (this.expn != null) {
			this.expn.accept(visitor);
		}
		visitor.visit(this);
	}
}

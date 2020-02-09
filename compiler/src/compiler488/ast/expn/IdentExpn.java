package compiler488.ast.expn;

import compiler488.semantics.ASTVisitor;

/**
 * References to a scalar variable or function call without parameters.
 */
public class IdentExpn extends ReadableExpn {
	/** Name of the identifier. */
	private String ident;

	public IdentExpn(String ident) {
		super();

		this.ident = ident;
		this.name = ident;
	}

	public String getIdent() {
		return ident;
	}

	/**
	 * Returns the name of the variable or function.
	 */
	@Override
	public String toString() {
		return ident;
	}

	@Override
	public void accept(ASTVisitor visitor) {
		visitor.visit(this);
	}

}

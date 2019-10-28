package compiler488.ast.expn;

import compiler488.semantics.AST_Visitor;

/**
 * Boolean literal constants.
 */
public class BoolConstExpn extends ConstExpn {
	/** The value of the constant */
	private boolean value;

	public BoolConstExpn(boolean value) {
		super();

		this.value = value;
	}

	public boolean getValue() {
		return value;
	}

	@Override
	public String toString() {
		return value ? "true" : "false";
	}

	@Override
	public void accept(AST_Visitor visitor) {
		visitor.visit(this);
	}
}

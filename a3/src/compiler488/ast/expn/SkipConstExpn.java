package compiler488.ast.expn;

import compiler488.ast.Printable;
import compiler488.semantics.ASTVisitor;

/**
 * Represents the special literal constant associated with writing a new-line
 * character on the output device.
 */
public class SkipConstExpn extends ConstExpn implements Printable {
	public SkipConstExpn() {
		super();
	}

	@Override
	public String toString() {
		return "newline";
	}

	@Override
	public void accept(ASTVisitor visitor) {
		visitor.visit(this);
	}
}

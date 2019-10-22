package compiler488.ast.expn;

import compiler488.ast.Readable;

/**
 * Reference to a bound given for array access / declaration
 * 
 * @author KyoKeun Park
 */
public class BoundExpr extends Expn implements Readable {
	private Expn upper;
	private Expn lower;

	public BoundExpr(Expn range) {
		super();

		this.lower = new IntConstExpn(1);
		this.upper = range;
	}

	public BoundExpr(Expn upper, Expn lower) {
		super();

		this.lower = lower;
		this.upper = upper;
	}

	public Expn getLowerBound() {
		return this.lower;
	}

	public Expn getUpperBound() {
		return this.upper;
	}

	// TODO: Maybe implement toString or prettyPrint here
}

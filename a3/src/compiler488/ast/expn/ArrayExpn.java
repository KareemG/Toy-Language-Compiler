package compiler488.ast.expn;

import compiler488.ast.Readable;
import compiler488.semantics.AST_Visitor;

/**
 * Reference to an access of array variable
 *
 * @author KyoKeun Park
 */
public class ArrayExpn extends IdentExpn {
	/** Array access number */
	private Expn index1;
	private Expn index2 = null;

	/** One dimensional array */
	public ArrayExpn(String ident, Expn index1) {
		super (ident);

		this.index1 = index1;
	}

	/** Two dimensional array */
	public ArrayExpn(String ident, Expn index1, Expn index2) {
		super(ident);

		this.index1 = index1;
		this.index2 = index2;
	}

	// TODO: Maybe implement toString or prettyPrint here

	@Override
	public void accept(AST_Visitor visitor) {
		visitor.visitEnter(this);
		this.index1.accept(visitor);
		if (this.index2 != null) {
			this.index2.accept(visitor);
		}
		visitor.visitLeave(this);
	}
}

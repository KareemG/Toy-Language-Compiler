package compiler488.ast.expn;

// import compiler488.ast.Readable;
import compiler488.semantics.AST_Visitor;

/**
 * References to a scalar variable or function call without parameters.
 */
public class IdentExpn extends ReadableExpn {
	/** Name of the identifier. */
	private String ident;

	public IdentExpn(String ident) {
		super();

		this.ident = ident;
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
	public void accept(AST_Visitor visitor) {
		visitor.visit(this);
	}

}

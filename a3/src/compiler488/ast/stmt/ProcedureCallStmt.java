package compiler488.ast.stmt;

import java.util.ListIterator;

import compiler488.ast.ASTList;
import compiler488.ast.PrettyPrinter;
import compiler488.ast.expn.Expn;
import compiler488.semantics.AST_Visitor;

/**
 * Represents calling a procedure.
 */
public class ProcedureCallStmt extends Stmt {
	/** The name of the procedure being called. */
	private String name;

	/**
	 * The arguments passed to the procedure (if any.)
	 *
	 * <p>
	 * This value must be non-<code>null</code>. If the procedure takes no
	 * parameters, represent that with an empty list here instead.
	 * </p>
	 */
	private ASTList<Expn> arguments = null;

	public ProcedureCallStmt(String name, ASTList<Expn> arguments) {
		super();

		this.name = name;
		this.arguments = arguments;
	}

	public ProcedureCallStmt(String name) {
		this(name, null);
	}

	public String getName() {
		return name;
	}

	public ASTList<Expn> getArguments() {
		return arguments;
	}

	@Override
	public void prettyPrint(PrettyPrinter p) {
		p.print(name);

		if ((arguments != null) && (arguments.size() > 0)) {
			p.print("(");
			arguments.prettyPrintCommas(p);
			p.print(")");
		}
	}

	@Override
	public void accept(AST_Visitor visitor) {
		visitor.visitEnter(this);
		if(arguments != null) {
			ListIterator<Expn> args = arguments.listIterator();
			while(args.hasNext()) {
				args.next().accept(visitor);
			}
		}
		visitor.visitLeave(this);
	}
}

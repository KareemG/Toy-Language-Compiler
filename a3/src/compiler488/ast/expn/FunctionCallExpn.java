package compiler488.ast.expn;

import java.util.ListIterator;

import compiler488.ast.ASTList;
import compiler488.ast.PrettyPrinter;
import compiler488.semantics.AST_Visitor;

/**
 * Represents a function call with arguments.
 */
public class FunctionCallExpn extends Expn {
	/** The name of the function. */
	private String ident;

	/** The arguments passed to the function. */
	private ASTList<Expn> arguments;

	public FunctionCallExpn(String ident, ASTList<Expn> arguments) {
		super();

		this.ident = ident;
		this.arguments = arguments;
	}

	public ASTList<Expn> getArguments() {
		return arguments;
	}

	public String getIdent() {
		return ident;
	}

	public void prettyPrint(PrettyPrinter p) {
		p.print(ident);

		if (arguments.size() > 0) {
			p.print("(");
			arguments.prettyPrintCommas(p);
			p.print(")");
		}
	}

	@Override
	public void accept(AST_Visitor visitor) {
		visitor.visitEnter(this);
		ListIterator<Expn> arg_lst = arguments.listIterator();
		while(arg_lst.hasNext()) {
			arg_lst.next().accept(visitor);;
		}
		visitor.visitLeave(this);
	}
}

package compiler488.ast.stmt;

import java.util.ListIterator;

import compiler488.ast.ASTList;
import compiler488.ast.PrettyPrinter;
import compiler488.ast.Readable;
import compiler488.semantics.AST_Visitor;

/**
 * The command to read data into one or more variables.
 */
public class ReadStmt extends Stmt {
	/** A list of locations to put the values read. */
	private ASTList<Readable> inputs;

	public ReadStmt(ASTList<Readable> inputs) {
		super();
		this.inputs = inputs;
	}

	@Override
	public void prettyPrint(PrettyPrinter p) {
		p.print("read ");
		inputs.prettyPrintCommas(p);
	}

	public ASTList<Readable> getInputs() {
		return inputs;
	}

	@Override
	public void accept(AST_Visitor visitor) {
		visitor.visitEnter(this);
		ListIterator<Readable> inps_lst = inputs.listIterator();
		while (inps_lst.hasNext()) {
			inps_lst.next().accept(visitor);
		}
		visitor.visitEnter(this);
	}
}

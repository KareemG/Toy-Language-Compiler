package compiler488.ast.stmt;

import java.util.ListIterator;

import compiler488.ast.ASTList;
import compiler488.ast.PrettyPrinter;
import compiler488.ast.expn.Expn;
import compiler488.semantics.AST_Visitor;

/**
 * Represents a loop in which the exit condition is evaluated before each pass.
 */
public class WhileDoStmt extends LoopingStmt {
	public WhileDoStmt(Expn expn, ASTList<Stmt> body) {
		super(expn, body);
	}

	@Override
	public void prettyPrint(PrettyPrinter p) {
		p.print("while ");
		expn.prettyPrint(p);
		p.println(" do");
		body.prettyPrintBlock(p);
	}

	@Override
	public void accept(AST_Visitor visitor) {
		visitor.visitEnter(this);
		this.expn.accept(visitor);
		ListIterator<Stmt> bod_lst = body.listIterator();
		while (bod_lst.hasNext()) {
			bod_lst.next().accept(visitor);
		}
		visitor.visitLeave(this);
	}
}

package compiler488.ast.decl;

import compiler488.ast.PrettyPrinter;
import compiler488.ast.type.Type;
import compiler488.semantics.ASTVisitor;

/**
 * Represents the declaration of a simple variable.
 */
public class ScalarDecl extends Declaration {
	public ScalarDecl(String name, Type type) {
		super(name, type);
	}

	@Override
	public void prettyPrint(PrettyPrinter p) {
		p.print(name + " : " + type);
	}

	@Override
	public void accept(ASTVisitor visitor) {
		visitor.visitEnter(this);
		this.type.accept(visitor);
		visitor.visitLeave(this);
	}
}

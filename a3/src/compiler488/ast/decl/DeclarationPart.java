package compiler488.ast.decl;

import compiler488.ast.BaseAST;
import compiler488.semantics.AST_Visitor;

/**
 * The common features of declarations' parts.
 */
public abstract class DeclarationPart extends BaseAST {
	/** The name of the thing being declared. */
	protected String name;

	public DeclarationPart(String name) {
		super();

		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public void accept(AST_Visitor visitor) {
		visitor.visit(this);
	}
}

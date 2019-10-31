package compiler488.ast.decl;

import compiler488.ast.BaseAST;
import compiler488.semantics.ASTVisitor;

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
}

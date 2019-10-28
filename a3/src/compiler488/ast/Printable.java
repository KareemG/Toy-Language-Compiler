package compiler488.ast;

import compiler488.semantics.AST_Visitor;

/**
 * Any AST node that can be an argument in a PUT statement.
 *
 * <p>
 * Don't confuse with the concept of printing the AST itself.
 * </p>
 */
public interface Printable extends AST {
    public void accept(AST_Visitor visitor);
}

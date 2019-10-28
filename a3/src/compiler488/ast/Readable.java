package compiler488.ast;

import compiler488.semantics.AST_Visitor;

/**
 * Any AST node that can be an argument in a GET statement.
 *
 * <p>
 * Don't confuse with concept with the printing of the AST itself.
 * </p>
 */
public interface Readable extends AST {
    public void accept(AST_Visitor visitor);
}

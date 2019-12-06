package compiler488.ast.expn;

import compiler488.ast.BaseAST;
import compiler488.ast.Printable;
import compiler488.ast.type.*;

/**
 * A placeholder for all expressions.
 */
public abstract class Expn extends BaseAST implements Printable {
    Type resultingType = null;

    public void setType(Type result) {
        this.resultingType = result;
    }

    public Type getType() {
        return this.resultingType;
    }
}

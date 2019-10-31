package compiler488.ast.expn;

import compiler488.ast.Readable;

public abstract class ReadableExpn extends Expn implements Readable {
    String name = null;

    public String getName() {
        return this.name;
    }
}
package compiler488.symbol;

import java.io.*;

import compiler488.ast.type.Type;

public class Context {
    private ContextType type;
    private int loopCount; // 0 if no nested loop
    private int retCount;
    private Type retType = null;

    private Context prev;

    public Context(Context prev, ContextType type) {
        this.type = type;
        this.prev = prev;
        this.retCount = 0;
        if (this.type == ContextType.MAIN) {
            this.loopCount = 0;
        } else if (this.type == ContextType.LOOP) {
            this.loopCount = this.prev.GetLoopCount() + 1;
        } else {
            this.loopCount = this.prev.GetLoopCount();
        }
    }

    public Context(Context prev, ContextType type, Type retType) {
        this.type = type;
        this.prev = prev;
        this.retCount = 0;
        if (this.type == ContextType.MAIN) {
            this.loopCount = 0;
        } else if (this.type == ContextType.LOOP) {
            this.loopCount = this.prev.GetLoopCount() + 1;
        } else {
            this.loopCount = this.prev.GetLoopCount();
        }
        this.retType = retType;
    }

    public ContextType GetType() {
        return this.type;
    }

    public int GetLoopCount() {
        return this.loopCount;
    }

    public int GetRetCount() {
        return this.retCount;
    }

    public void IncrementRet() {
        this.retCount++;
    }

    public Context GetPrev() {
        return this.prev;
    }

    public Type GetRetType() {
        return this.retType;
    }
}
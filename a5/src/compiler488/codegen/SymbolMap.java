package compiler488.codegen;

import java.io.*;
import java.util.*;
import compiler488.compiler.Main;
import compiler488.runtime.Machine;
import compiler488.runtime.MemoryAddressException;
import compiler488.ast.*;
import compiler488.ast.decl.*;
import compiler488.ast.expn.*;
import compiler488.ast.stmt.*;
import compiler488.ast.type.*;
import compiler488.semantics.ASTVisitor;
import java.util.function.Consumer;
import java.util.function.BiConsumer;

public class SymbolMap
{
    public static class Entry
    {
        public static enum TYPE
        {
            SCALAR,
            ARRAY_1D,
            ARRAY_2D,
            FUNCTION,
            PROCEDURE
        }

        public TYPE type;
        public Scope parent;

        public Entry(TYPE type) {
            this.type = type;
        }
    }

    public static class Scalar extends Entry
    {
        public short register;

        public Scalar(short register) {
            super(Entry.TYPE.SCALAR);
            this.register = register;
        }
    }

    public static class Array1D extends Entry
    {
        public short base_register, offset;

        public Array1D(short base_register, short offset) {
            super(Entry.TYPE.ARRAY_1D);
            this.offset = offset;
            this.base_register = base_register;
        }
    }

    public static class Array2D extends Entry
    {
        public short base_register, offset1, offset2, stride;

        public Array2D(short base_register, short offset1, short offset2, short stride) {
            super(Entry.TYPE.ARRAY_2D);
            this.offset1 = offset1;
            this.offset2 = offset2;
            this.stride = stride;
            this.base_register = base_register;
        }
    }

    public static class Procedure extends Entry
    {
        public short address;

        public Procedure(short address) {
            super(Entry.TYPE.PROCEDURE);
        }
    }

    public static class Function extends Entry
    {
        public short address;

        public Function(short address) {
            super(Entry.TYPE.FUNCTION);
        }
    }

    public static class Scope
    {
        public Scope parent;
        public short lexical_level;
        public HashMap<String, Entry> entries;

        public Scope(short lexical_level, Scope parent)
        {
            this.parent = parent;
            this.lexical_level = lexical_level;
            this.entries = new HashMap<>();
        }

        public Entry search(String symbol)
        {
            Entry entry = this.entries.get(symbol);
            if((entry == null) && (this.parent != null))
            {
                entry = this.parent.search(symbol);
            }
            return entry;
        }
    }

    private Scope current_scope;

    SymbolMap() {
        this.current_scope = new Scope((short) 0, null);
    }

    void push() {
        current_scope = new Scope((short) (current_scope.lexical_level + 1), current_scope);
    }

    void pop() {
        current_scope = current_scope.parent;
    }

    void insert(String name, Entry entry) {
        current_scope.entries.put(name, entry);
        entry.parent = current_scope;
    }

    Entry search(String symbol) {
        return current_scope.search(symbol);
    }
}

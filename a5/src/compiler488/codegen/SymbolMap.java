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

public class SymbolMap {
  public static abstract class Entry {
    public static enum TYPE {
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

    public abstract short getSize();
  }

  public static class Scalar extends Entry {
    public short register;

    public Scalar(short register) {
      super(Entry.TYPE.SCALAR);
      this.register = register;
    }

    @Override
    public short getSize() {
      return 1;
    }
  }

  public static class Array1D extends Entry {
    public short base_register, offset;
    public short size;

    public Array1D(
      short base_register,
      short offset,
      short size) {
      super(Entry.TYPE.ARRAY_1D);
      this.offset = offset;
      this.base_register = base_register;
      this.size = size;
    }

    @Override
    public short getSize() {
      return this.size;
    }
  }

  public static class Array2D extends Entry {
    public short base_register, offset1, offset2, stride;
    public short size;

    public Array2D(
      short base_register,
      short offset1,
      short offset2,
      short stride,
      short size) {
      super(Entry.TYPE.ARRAY_2D);
      this.offset1 = offset1;
      this.offset2 = offset2;
      this.stride = stride;
      this.base_register = base_register;
      this.size = size;
    }

    @Override
    public short getSize() {
      return this.size;
    }
  }

  public static class Procedure extends Entry {
    public short address;

    public Procedure(short address) {
      super(Entry.TYPE.PROCEDURE);
    }

    @Override
    public short getSize() {
      return 0;
    }
  }

  public static class Function extends Entry {
    public short address;

    public Function(short address) {
      super(Entry.TYPE.FUNCTION);
    }

    @Override
    public short getSize() {
      return 0;
    }
  }

  public static class Scope {
    public Scope parent;
    public short lexical_level;
    public HashMap<String, Entry> entries;
    public HashMap<String, Scalar> exprs;

    public Scope(short lexical_level, Scope parent) {
      this.parent = parent;
      this.lexical_level = lexical_level;
      this.entries = new HashMap<>();
      this.exprs = new HashMap<>();
    }

    public Entry search(String symbol) {
      Entry entry = this.entries.get(symbol);
      if ((entry == null) && (this.parent != null)) {
        entry = this.parent.search(symbol);
      }
      return entry;
    }

    public short getSize() {
      short size = 0;
      for (Entry e : this.entries.values()) {
        size += e.getSize();
      }
      System.out.println(size);
      for (Entry e : this.exprs.values()) {
        size += e.getSize();
      }
      System.out.println(size);
      System.out.println("");
      return size;
    }
  }

  private Scope current_scope;

  SymbolMap() {
    current_scope = new Scope((short) 0, null);
  }

  void push_minor() {
    current_scope = new Scope(
      (short) (current_scope.lexical_level), current_scope);
  }

  void push() {
    current_scope = new Scope(
      (short) (current_scope.lexical_level + 1), current_scope);
  }

  void pop() {
    current_scope = current_scope.parent;
  }

  void insert(String name, Entry entry) {
    current_scope.entries.put(name, entry);
    entry.parent = current_scope;
  }

  void cache(String expr, Scalar entry) {
    current_scope.exprs.put(expr, entry);
    entry.parent = current_scope;
  }

  Entry search(String symbol) {
    return current_scope.search(symbol);
  }

  short getSize() {
    return current_scope.getSize();
  }
}

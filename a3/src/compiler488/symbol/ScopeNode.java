package compiler488.symbol;

import java.io.*;
import java.util.*;
import compiler488.ast.*;
import compiler488.ast.decl.*;
import compiler488.ast.type.*;

/** Scope Node
 *  This will represent a single instance of a scope.
 *
 *  Holds currently declared items within a given symbol.
 *
 *  Also has access to its parent so we can look back for any
 *  previously declared variables or functions
 *
 * @author  KyoKeun Park
 */
class ScopeNode implements PrettyPrintable {
    private Hashtable<String, BaseAST> symbols; // declared stuff in curr scope
    private Hashtable<String, Record> syms;
    private ArrayList<ScopeNode> archive; // essentially children of given scope
    private ScopeNode parent = null; // reference to parent
    protected String label = null; // scope node label (functions and procedures)
    protected Type type = null; // return type of the scope (for functions)
    protected ASTList<ScalarDecl> params = null; // params (for functions)
    protected ArrayList<Record> param = null;

    public ScopeNode() {
        Initialize();
    }

    public ScopeNode(ScopeNode parent) {
        Initialize();
        this.parent = parent;
    }

    public ScopeNode(ScopeNode parent, String label) {
        Initialize();
        this.parent = parent;
    }

    public ScopeNode(ScopeNode parent, String label, ASTList<ScalarDecl> params) {
        this.parent = parent;
        this.label = label;
        this.params = params;
    }

    public ScopeNode(ScopeNode parent, String label,
                        Type type, ASTList<ScalarDecl> params) {
        Initialize();
        this.parent = parent;
        this.label = label;
        this.type = type;
        this.params = params;
    }

    private void Initialize() {
        this.symbols = new Hashtable<String, BaseAST>();
        this.archive = new ArrayList<ScopeNode>();
        this.syms = new Hashtable<>();
    }

    public int Put(String key, BaseAST value) {
        if (this.symbols.containsKey(key)) {
            return 1;
        }
        this.symbols.put(key, value);
        return 0;
    }

    public int put(String key, Record value) {
        if (this.syms.containsKey(key)) {
            return 1;
        }
        this.syms.put(key, value);
        return 0;
    }

    public BaseAST Get(String key) {
        return this.symbols.get(key);
    }

    public Record get(String key) {
        return this.syms.get(key);
    }

    public void addParam(Record rec) {
    }

    public void AddArchive(ScopeNode node) {
        this.archive.add(node);
    }

    public ScopeNode GetParent() {
        return this.parent;
    }

    @Override
    public void prettyPrint(PrettyPrinter p) {
        // Print out all of the metadata (if applicable)
        if(label != null) {
            p.print(label);
        }
        if(type != null) {
            p.print(" : " + type.toString());
        }
        if(params != null) {
            p.print("(");
            params.prettyPrintCommas(p);
            p.print(")");
        }
        p.println("{");
        p.enterBlock();

        // Print all of the symbols
        p.println("Symbols:");
        p.enterBlock();
        Set<String> keys = syms.keySet();
        for (String key : keys) {
            Record toPrint = syms.get(key);
            if (toPrint.getType() != RecordType.PARAMETER) {
                p.println(toPrint.getIdent() + ": " + toPrint.getResult().toString());
            }
        }
        // Set<String> keys = symbols.keySet();
        // for (String key:keys) {
        //     if (!key.equals(label)) {
        //         symbols.get(key).prettyPrint(p);
        //     }
        // }
        p.exitBlock();

        // Print children ScopeNodes
        for(ScopeNode child : archive) {
            child.prettyPrint(p);
        }

        p.exitBlock();
        p.println("}");
    }
}
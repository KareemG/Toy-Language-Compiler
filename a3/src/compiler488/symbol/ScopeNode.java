package compiler488.symbol;

import java.io.*;
import java.util.*;
import compiler488.ast.*;

/** Scope Node
 *  This will represent a single instance of a scope.
 * 
 *  Holds currently declared items within a given symbol.
 * 
 *  Also has access to its parent so we can look back for any
 *  previously declared variables or functions
 */
class ScopeNode {
    private Hashtable<String, BaseAST> symbols; // declared stuff in curr scope
    private ArrayList<ScopeNode> archive; // essentially children of given scope
    private ScopeNode parent = null; // reference to parent

    ScopeNode() {
        this.symbols = new Hashtable<String, BaseAST>();
    }

    ScopeNode(ScopeNode parent) {
        this.symbols = new Hashtable<String, BaseAST>();
        this.parent = parent;
    }
    
    public int Put(String key, BaseAST value) {
        if (this.symbols.containsKey(key)) {
            return 1;
        }
        this.symbols.put(key, value);
        return 0;
    }

    public BaseAST Get(String key) {
        return this.symbols.get(key);
    }

    public void AddArchive(ScopeNode node) {
        this.archive.add(node);
    }

    public ScopeNode GetParent() {
        return this.parent;
    }
}
package compiler488.symbol;

import java.io.*;
import java.util.*;
import compiler488.ast.*;

class ScopeNode {
    Hashtable<String, BaseAST> symbols;
    ArrayList<ScopeNode> archive;
    ScopeNode parent = null;

    ScopeNode() {
        symbols = new Hashtable<String, BaseAST>();
    }

    ScopeNode(ScopeNode parent) {
        symbols = new Hashtable<String, BaseAST>();
        this.parent = parent;
    }
    
    public int Put(String key, BaseAST value) {
        if (symbols.containsKey(key)) {
            return 1;
        }
        symbols.put(key, value);
        return 0;
    }

    public BaseAST Get(String key) {
        return symbols.get(key);
    }

    public void AddArchive(ScopeNode node) {
        this.archive.add(node);
    }

    public ScopeNode GetParent() {
        return this.parent;
    }
}
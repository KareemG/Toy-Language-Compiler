package compiler488.symbol;

import java.util.*;

import compiler488.ast.type.*;

/**
 * A single record within a SymbolTable/ScopeNode.
 * Used to store a single declaration and its type
 */
public class Record {
    private String ident;
    private RecordType type;
    private Type result;
    private ArrayList<Record> params = null;

    public Record(String ident) {
        this.ident = ident;
    }

    public Record(String ident, RecordType type) {
        this.ident = ident;
        this.type = type;
    }

    public void setType(RecordType type) {
        this.type = type;
    }

    public void setResult(Type type) {
        this.result = type;
    }

    public void initParam() {
        this.params = new ArrayList<Record>();
    }

    public void addParam(Record rec) {
        this.params.add(rec);
    }

    public void setParam(ArrayList<Record> params) {
        this.params = params;
    }

    public String getIdent() {
        return this.ident;
    }

    public RecordType getType() {
        return this.type;
    }

    public int getParamCount() {
        if(this.params != null) {
            return this.params.size();
        }
        return -1;
    }

    public ArrayList<Record> getParams() {
        return this.params;
    }

    public Type getResult() {
        return this.result;
    }
}
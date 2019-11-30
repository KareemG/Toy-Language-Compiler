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
    private int size;
    private int orderNumber;

    public Record(String ident, RecordType type, int size) {
        this.ident = ident;
        this.type = type;
        this.size = size;
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

    public void setOrderNumber(int number) {
        this.orderNumber = number;
    }

    public String getIdent() {
        return this.ident;
    }

    public RecordType getType() {
        return this.type;
    }

    public int getOrderNumber() {
        return this.orderNumber;
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

    public int getSize() {
        return this.size;
    }
}
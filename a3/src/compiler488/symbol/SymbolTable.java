package compiler488.symbol;

import java.util.ArrayList;

import compiler488.ast.*;
import compiler488.ast.decl.ScalarDecl;
import compiler488.ast.type.*;

/** Symbol Table
 *  This almost empty class is a framework for implementing
 *  a Symbol Table class for the CSC488S compiler
 *
 *  Each implementation can change/modify/delete this class
 *  as they see fit.
 *
 *  @author  KyoKeun Park
 */

public class SymbolTable implements PrettyPrintable {
	private ScopeNode root;

	/** Symbol Table  constructor
         *  Create and initialize a symbol table
	 */
	public SymbolTable(){
		this.root = new ScopeNode(null);
	}

	/**  Initialize - called once by semantic analysis
	 *                at the start of  compilation
	 *                May be unnecessary if constructor
 	 *                does all required initialization
	 */
	public void Initialize() {

	   /**   Initialize the symbol table
	    *	Any additional symbol table initialization
	    *  GOES HERE
		*/

	}

	/**  Finalize - called once by Semantics at the end of compilation
	 *              May be unnecessary
	 */
	public void Finalize(){

	  /**  Additional finalization code for the
	   *  symbol table  class GOES HERE.
	   *
	   */
	}


	/** The rest of Symbol Table
	 *  Data structures, public and private functions
 	 *  to implement the Symbol Table
	 *  GO HERE.
	 */

	/** Get a given key - value combination from the symbol table.
	 *  If not present, asserts
	 *  @param key variable name of interest
	 */
	public BaseAST Get(String key) {
		ScopeNode traverse = root;
		while (traverse != null) {
			BaseAST value = traverse.Get(key);
			if (value != null) {
				return value;
			}
			traverse = traverse.GetParent();
		}
		assert(false);
		return null;
	}
	public Record get(String key) {
		ScopeNode traverse = root;
		while (traverse != null) {
			Record value = traverse.get(key);
			if (value != null) {
				return value;
			}
			traverse = traverse.GetParent();
		}
		if (root.param != null) {
			for (Record p : root.param) {
				if (p.getIdent() == key) {
					return p;
				}
			}
		}
		assert(false);
		return null;
	}

	/** Put a given key - value combination into the symbol table
	 *  Asserts if the key is already present
	 *  @param key variable name binded to the val
	 *  @param val actual object that has been declared
	 */
	public void Put(String key, BaseAST val) {
		assert(this.root.Put(key, val) == 0);
	}

	public void put(String key, Record val) {
		int ret = this.root.put(key, val);
		assert(ret == 0);
	}

	public void pPut(String key, Record val) {
		int ret = this.root.GetParent().put(key, val);
		assert(ret == 0);
	}

	/** Call when entering a new scope.
	 *  Creates another hashtable/ScopeNode, and pushes it on top of
	 *  stack of nodes.
	 */
	public void EnterScope() {
		ScopeNode newScope = new ScopeNode(this.root);
		this.root = newScope;
	}

	public void enterScope() {
		ScopeNode newScope = new ScopeNode(this.root);
		this.root = newScope;
	}

	public void enterScope(String label, Type type) {
		ScopeNode newScope = new ScopeNode(this.root, label);
		this.root = newScope;
		this.root.type = type;
	}
	public void enterScope(String label) {
		ScopeNode newScope = new ScopeNode(this.root, label);
		this.root = newScope;
	}

	/**
	 * Enter procedure scope.
	 * @param label name of the procedure
	 */
	public void EnterScope(String label, ASTList<ScalarDecl> params) {
		ScopeNode newScope = new ScopeNode(this.root, label, params);
		this.root = newScope;
	}

	/**
	 * Enter function scope.
	 * @param label name of the function
	 * @param type return type of the function
	 * @param params parameters of the function
	 */
	public void EnterScope(String label, Type type, ASTList<ScalarDecl> params) {
		ScopeNode newScope = new ScopeNode(this.root, label, type, params);
		this.root = newScope;
	}

	/** Call when exiting the current scope.
	 *
	 *  Sets the root to current scope's parent.
	 *
	 *  Gives the parent reference to the current scope.
	 *  This may not seem necessary, but may require in the future if
	 *  we need to create a symbol file or something like that.
	 *  Can always be removed if deemed unnecessary
	 */
	public void ExitScope() {
		// Only exit if you are not the main scope
		if (this.root != null) {
			ScopeNode tmp = this.root;
			this.root = root.GetParent();
			this.root.AddArchive(tmp);
		}
	}
	public void exitScope() {
		// Only exit if you are not the main scope
		if (this.root != null) {
			ScopeNode tmp = this.root;
			this.root = root.GetParent();
			this.root.AddArchive(tmp);
		}
	}

	public String GetLabel() {
		return this.root.label;
	}

	public Type GetType() {
		return this.root.type;
	}

	public ASTList<ScalarDecl> GetParams() {
		return this.root.params;
	}

	public void initParams() {
		this.root.param = new ArrayList<Record>();
	}

	public void addParams(Record p) {
		this.root.param.add(p);
	}

	public ArrayList<Record> getParams() {
		return this.root.param;
	}

	public ScopeNode getParent() {
		return this.root.GetParent();
	}

	@Override
	public void prettyPrint(PrettyPrinter p) {
		assert(this.root != null);
		this.root.prettyPrint(p);
	}
}

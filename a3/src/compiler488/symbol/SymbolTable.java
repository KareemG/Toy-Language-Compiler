package compiler488.symbol;

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

public class SymbolTable {
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

	/** Put a given key - value combination into the symbol table
	 *  Asserts if the key is already present
	 *  @param key variable name binded to the val
	 *  @param val actual object that has been declared
	 */
	public void Put(String key, BaseAST val) {
		assert(this.root.Put(key, val) == 0);
	}

	/** Call when entering a new scope.
	 *  Creates another hashtable/ScopeNode, and pushes it on top of
	 *  stack of nodes.
	 */
	public void EnterScope() {
		ScopeNode newScope = new ScopeNode(this.root);
		this.root = newScope;
	}

	/**
	 * Enter procedure scope.
	 * @param label name of the procedure
	 */
	public void EnterScope(String label) {
		ScopeNode newScope = new ScopeNode(this.root, label);
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
		assert(this.root != null);
		ScopeNode tmp = this.root;
		this.root = root.GetParent();
		this.root.AddArchive(tmp);
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
}

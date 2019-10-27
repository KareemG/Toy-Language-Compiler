package compiler488.symbol;

import java.io.*;
import compiler488.ast.*;

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
	private ScopeNode root = null;

	/** Symbol Table  constructor
         *  Create and initialize a symbol table 
	 */
	public SymbolTable  (){
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
	 *  TODO: Maybe just throw an error here instead...
	 */
	public BaseAST Get(String key) {
		return this.root.Get(key);
	}

	/** Put a given key - value combination into the symbol table
	 *  Returns 0 if successful, 1 if not.
	 *  TODO: Maybe just throw an error here instead...
	 */
	public void Put(String key, BaseAST val) {
		this.root.Put(key, val);
	}

	/** Call when entering a new scope.
	 *  Creates another hashtable/ScopeNode, and pushes it on top of
	 *  stack of nodes.
	 */
	public void EnterScope() {
		ScopeNode newScope = new ScopeNode(this.root);
		this.root = newScope;
	}

	public void ExitScope() {
		assert(this.root != null);
		ScopeNode tmp = this.root;
		this.root = root.GetParent();
		this.root.AddArchive(tmp);
	}

}

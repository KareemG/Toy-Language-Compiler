package compiler488.symbol;

import java.io.*;
import java.util.*;
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
	private Hashtable<String, BaseAST> symbols = null;
	private SymbolTable next;
	
	/** Symbol Table  constructor
         *  Create and initialize a symbol table 
	 */
	public SymbolTable  (){
		this.Initialize();
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
		symbols = new Hashtable<String, BaseAST>();
	
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
	ScopeNode root;

	static class ScopeNode {
		Hashtable<String, BaseAST> symbols;
		ScopeNode next;

		ScopeNode() {
			symbols = new Hashtable<String, BaseAST>();
			next = null;
		}
	}

	/** Get a given key - value combination from the symbol table.
	 *  If not present, asserts
	 *  TODO: Maybe just throw an error here instead...
	 */
	public ASTBase Get(String key) {
		assert(symbols.containsKey(key));
		return symbols.get(key);
	}

	/** Put a given key - value combination into the symbol table
	 *  Returns 0 if successful, 1 if not.
	 *  TODO: Maybe just throw an error here instead...
	 */
	public void Put(String key, ASTBase val) {
		assert(!symbols.containsKey(key));
		symbols.put(key, val);
	}

	public void EnterScope() {
		ScopeNode newScope = new ScopeNode();
		if (root == null) {
			root = newScope;
		} else {
			ScopeNode tmp = root;
			root = newScope;
			root.next = tmp;
		}
	}

	public void ExitScope() {
		assert(root != null);
		root = root.next;
	}

}

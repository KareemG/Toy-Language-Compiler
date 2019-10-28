package compiler488.semantics;

import java.io.*;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;

import compiler488.ast.BaseAST;
import compiler488.ast.decl.RoutineDecl;
import compiler488.ast.stmt.ExitStmt;
import compiler488.ast.stmt.LoopingStmt;
import compiler488.ast.stmt.Program;
import compiler488.ast.stmt.ReturnStmt;
import compiler488.ast.stmt.Scope;
import compiler488.symbol.SymbolTable;

/**
 * Implement semantic analysis for compiler 488
 * 
 * @author <B> Put your names here </B>
 */
public class Semantics extends AST_Visitor.Default {

	/** flag for tracing semantic analysis */
	private boolean traceSemantics = false;
	/** file sink for semantic analysis trace */
	private String traceFile = new String();
	public FileWriter Tracer;
	public File f;

	private SymbolTable symTable;
	private Map<Integer, BiConsumer<List<BaseAST>, Semantics>> analyzers;
	private List<BaseAST> analyzee;

	/** SemanticAnalyzer constructor */
	public Semantics() {

	}

	/** semanticsInitialize - called once by the parser at the */
	/* start of compilation */
	void Initialize() {

		/*********************************************/
		/* Additional initialization code for the */
		/* semantic analysis module */
		/* GOES HERE */
		/*********************************************/

		this.symTable = new SymbolTable();
		this.analyzers = new HashMap<>();
		this.analyzee = null;

		this.symTable.Initialize();

		// TODO: We should add bunch of semantics actions here maybe?
	}

	/** semanticsFinalize - called by the parser once at the */
	/* end of compilation */
	void Finalize() {

		/*********************************************/
		/* Additional finalization code for the */
		/* semantics analysis module */
		/* GOES here. */
		/**********************************************/
		this.symTable.Finalize();

	}

	/**
	 * Perform one semantic analysis action
	 * 
	 * @param actionNumber semantic analysis action number
	 */
	void semanticAction(int actionNumber, BaseAST... nodes) {

		if (traceSemantics) {
			if (traceFile.length() > 0) {
				// output trace to the file represented by traceFile
				try {
					// open the file for writing and append to it
					File f = new File(traceFile);
					Tracer = new FileWriter(traceFile, true);

					Tracer.write("Sematics: S" + actionNumber + "\n");
					// always be sure to close the file
					Tracer.close();
				} catch (IOException e) {
					System.out.println(traceFile + " could be opened/created.  It may be in use.");
				}
			} else {
				// output the trace to standard out.
				System.out.println("Sematics: S" + actionNumber);
			}

		}

		/*************************************************************/
		/* Code to implement each semantic action GOES HERE */
		/* This stub semantic analyzer just prints the actionNumber */
		/*                                                           */
		/* FEEL FREE TO ignore or replace this procedure */
		/*************************************************************/

		assert(analyzee != null);
		System.out.println("Semantic Action: S" + actionNumber);
		this.analyzers.get(actionNumber).accept(Arrays.asList(nodes), this);
		return;
	}

	@Override
	public void visitEnter(Program prog) {
		semanticAction(0, prog);
	}

	@Override
	public void visitLeave(Program prog) {
		semanticAction(1, prog);
	}

	@Override
	public void visitEnter(Scope scope) {
		if (scope.getDeclarations() != null) {
			semanticAction(2, scope);
		}
	}

	@Override
	public void visitLeave(Scope scope) { } // Nothing to do

	@Override
	public void visitEnter(RoutineDecl node) {
	}

	@Override
	public void visitLeave(RoutineDecl node) {
	}

	@Override
	public void visitEnter(LoopingStmt loopStmt) {
		if(loopStmt.getExpn() != null) {
			semanticAction(30, loopStmt.getExpn());
		}
	}

	@Override
	public void visitLeave(LoopingStmt node) { } // Nothing to do

	@Override
	public void visit(ExitStmt exitStmt) {
		if (exitStmt.getExpn() != null) {
			semanticAction(30, exitStmt.getExpn());
		}
		semanticAction(50, exitStmt);
		if (exitStmt.getLevel() != -1) {
			semanticAction(53, exitStmt);
		}
	}

	@Override
	public void visit(ReturnStmt retStmt) {
		if (retStmt.getValue() != null) {
			semanticAction(51, retStmt);
			semanticAction(35, retStmt);
		} else {
			semanticAction(52, retStmt);
		}
	}

	// ADDITIONAL FUNCTIONS TO IMPLEMENT SEMANTIC ANALYSIS GO HERE

}

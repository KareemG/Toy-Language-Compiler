package compiler488.semantics;

import java.io.*;
import java.util.Map;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.function.BiConsumer;

import compiler488.ast.BaseAST;
import compiler488.ast.decl.*;
import compiler488.ast.stmt.*;
import compiler488.ast.type.*;
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

		this.symTable.Initialize();

		// TODO: We should add bunch of semantics actions here maybe?

		// Start program scope.
		analyzers.put(0, (s, self) -> {
			assert(s.get(0) instanceof Program);
		});

		// End program scope.
		analyzers.put(1, (s, self) -> {
			assert(s.get(0) instanceof Program);
		});

		// Associate declaration(s) with scope.
		analyzers.put(2, (s, self) -> {
			assert(s.get(0) instanceof Scope);
			
			Scope scope = (Scope) s.get(0);
			ListIterator<Declaration> decl_it = scope.getDeclarations().listIterator();
			while (decl_it.hasNext()) {
				Declaration decl = decl_it.next();
				if (decl instanceof MultiDeclarations) {
					MultiDeclarations multi_decl = (MultiDeclarations) decl;
					
					ListIterator<DeclarationPart> part_it = multi_decl.getParts().listIterator();
					while (part_it.hasNext()) {
						DeclarationPart part = part_it.next();
						self.symTable.Put(part.getName(), part);
					}
				} else if (decl instanceof RoutineDecl) {
					RoutineDecl routine = (RoutineDecl) decl;
					self.symTable.Put(routine.getName(), routine);
				} else {
					assert(false);
				}
			}
		});

		// Start function scope.
		analyzers.put(4, (s, self) -> { self.EnterScope(s); });

		// End function scope.
		analyzers.put(5, (s, self) -> { self.ExitScope(s);  });

		// Start ordinary scope.
		analyzers.put(6, (s, self) -> { self.EnterScope(s); });

		// End ordinary scope.
		analyzers.put(7, (s, self) -> { self.ExitScope(s);  });

		// Start procedure scope.
		analyzers.put(8, (s, self) -> { self.EnterScope(s); });

		// End procedure scope.
		analyzers.put(9, (s, self) -> { self.ExitScope(s);  });

		// Declare scalar variable.
		analyzers.put(10, (s, self) -> {
			assert(s.get(0) instanceof ScalarDeclPart);

			ScalarDeclPart decl = (ScalarDeclPart) s.get(0);
			self.symTable.Put(decl.getName(), decl);
		});

		// Declare function with no parameters and specified type.
		analyzers.put(11, (s, self) -> {
			assert(s.get(0) instanceof RoutineDecl);

			RoutineDecl decl = (RoutineDecl) s.get(0);
			self.symTable.Put(decl.getName(), decl);

			// TODO(golaubka): Record scope type.

		});

		// Declare function with parameters and specified type.
		analyzers.put(12, analyzers.get(11));

		// Associate scope with function/procedure.
		analyzers.put(13, (s, self) -> { self.EnterScope(s); });

		// Set parameter count to zero.
		analyzers.put(14, (s, self) -> {
			// TODO(golaubka): y tho? codegen?
		});

		// Declare parameter with specified type.
		analyzers.put(15, analyzers.get(10));

		// Increment parameter count by one.
		analyzers.put(16, (s, self) -> {
			// TODO(golaubka): y tho? codegen?
		});

		// Declare procedure with no parameters.
		analyzers.put(17, (s, self) -> analyzers.get(11));

		// Declare procedure with parameters.
		analyzers.put(18, (s, self) -> analyzers.get(12));

		// Declare array variable with specified lower and upper bounds.
		analyzers.put(19, (s, self) -> {
			assert(s.get(0) instanceof ArrayDeclPart);

			ArrayDeclPart decl = (ArrayDeclPart) s.get(0);
			self.symTable.Put(decl.getName(), decl);
		});

		// Check that lower bound is <= upper bound.
		analyzers.put(46, (s, self) -> {
			assert(s.get(0) instanceof ArrayDeclPart);

			ArrayDeclPart decl = (ArrayDeclPart) s.get(0);

			assert(decl.lb1 <= decl.ub1);
			assert(!decl.isTwoDimensional || (decl.lb2 <= decl.ub2));
		});

		// Associate type with variables.
		analyzers.put(47, (s, self) -> {
			// TODO(golaubka): Seems like the type is already stored in the associated Decl class.
		});
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

		System.out.println("Semantic Action: S" + actionNumber);
		this.analyzers.get(actionNumber).accept(Arrays.asList(nodes), this);
		return;
	}

	// ===== PROGRAM AND SCOPE ===== //
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

	// ===== DECLARATIONS ===== //

	@Override
	public void visitEnter(RoutineDecl routine) {
		if(routine.getType() != null) { // Routine is a function
			if(routine.getParameters() != null) {
				semanticAction(4, routine);
				semanticAction(14, routine);
				semanticAction(15, routine);
			} else {
				semanticAction(11, routine);
				semanticAction(4, routine);
			}
		} else { // Routine is a procedure
			if(routine.getParameters() != null) {
				semanticAction(8, routine);
				semanticAction(14, routine);
				semanticAction(18, routine);
			} else {
				semanticAction(17, routine);
				semanticAction(18, routine);
			}
		}
	}
	@Override
	public void visitLeave(RoutineDecl routine) {
		if(routine.getType() != null) {
			semanticAction(5, routine);
			semanticAction(54, routine);
		} else {
			semanticAction(9, routine);
		}
		semanticAction(13, routine);
	}

	@Override
	public void visitLeave(MultiDeclarations decls) {
		ListIterator<DeclarationPart> dec_part = decls.getParts().listIterator();
		while(dec_part.hasNext()) {
			semanticAction(47, dec_part.next(), decls.getType());
		}
	}

	// ===== STATEMENTS ===== //

	@Override
	public void visitLeave(AssignStmt assign) {
		semanticAction(34, assign.getLval(), assign.getRval());
	}

	@Override
	public void visitEnter(ProcedureCallStmt procStmt) {
		semanticAction(42, procStmt);
	}

	@Override
	public void visitEnter(LoopingStmt loopStmt) {
		if(loopStmt.getExpn() != null) {
			semanticAction(30, loopStmt.getExpn());
		}
	}

	// ===== NON LEAF NODES ===== //

	@Override
	public void visitEnter(IfStmt ifStmt) {
		semanticAction(30, ifStmt.getCondition());
	}

	@Override
	public void visit(ArrayDeclPart arrPart) {
		semanticAction(19, arrPart);
	}

	@Override
	public void visit(ScalarDeclPart scaPart) {
		semanticAction(10, scaPart);
	}

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

	@Override
	public void visit(BooleanType boolType) {
		semanticAction(20, boolType);
	}
	@Override
	public void visit(IntegerType intType) {
		semanticAction(21, intType);
	}

	// ADDITIONAL FUNCTIONS TO IMPLEMENT SEMANTIC ANALYSIS GO HERE

	public void EnterScope(List<BaseAST> s)
	{
		symTable.EnterScope();
	}

	public void ExitScope(List<BaseAST> s)
	{
		symTable.ExitScope();
	}
	
}

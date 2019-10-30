package compiler488.semantics;

import java.io.*;
import java.util.Map;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.function.BiConsumer;

import compiler488.ast.*;
import compiler488.ast.stmt.*;
import compiler488.ast.type.*;
import compiler488.ast.expn.*;
import compiler488.ast.decl.*;
import compiler488.symbol.*;

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
	private Context context;
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

		this.context = new Context(null, ContextType.MAIN);
		this.symTable = new SymbolTable();
		this.analyzers = new HashMap<>();

		this.symTable.Initialize();

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

		// ===== EXPRESSION TYPE CHECKING ===== //
		analyzers.put(30, (s, self) -> {
			assert(s.get(0) instanceof Expn);
			Expn expn = (Expn) s.get(0);
			assert(expn.getType() instanceof BooleanType);
		});
		analyzers.put(31, (s, self) -> {
			assert(s.get(0) instanceof Expn);
			Expn expn = (Expn) s.get(0);
			assert(expn.getType() instanceof IntegerType);
		});
		analyzers.put(32, (s, self) -> {
			assert(s.get(0) instanceof Expn);
			assert(s.get(1) instanceof Expn);
			assert(((Expn) s.get(0)).getType().equals(((Expn) s.get(1)).getType()));
		});
		analyzers.put(33, (s, self) -> {
			// TODO: Redundant perhaps?
			assert(s.get(0) instanceof Expn);
			assert(s.get(1) instanceof Expn);
			assert(((Expn) s.get(0)).getType().equals(((Expn) s.get(1)).getType()));
		});
		analyzers.put(34, (s, self) -> {
			assert(s.get(0) instanceof IdentExpn);
			assert(s.get(1) instanceof Expn);
			assert(((IdentExpn) s.get(0)).getType().equals(((Expn) s.get(1)).getType()));
		});
		analyzers.put(35, (s, self) -> {
			assert(s.get(0) instanceof ReturnStmt);
			assert(symTable.GetType() != null);
			assert(((ReturnStmt) s.get(0)).getValue().getType().equals(symTable.GetType()));
		});
		analyzers.put(36, (s, self) -> {});
		analyzers.put(37, (s, self) -> {});
		analyzers.put(38, (s, self) -> {});
		analyzers.put(39, (s, self) -> {});

		// ===== FUNCTIONS, PROCEDURES AND ARGUMENTS ===== //
		analyzers.put(40, (s, self) -> {
			assert(s.get(0) instanceof FunctionCallExpn);
			FunctionCallExpn funcExpn = (FunctionCallExpn) s.get(0);
			assert(symTable.Get(funcExpn.getIdent()) instanceof RoutineDecl);
			RoutineDecl routine = (RoutineDecl) symTable.Get(funcExpn.getIdent());
			assert(routine.getType() != null);
		});
		analyzers.put(41, (s, self) -> {
			assert(s.get(0) instanceof ProcedureCallStmt);
			ProcedureCallStmt procStmt = (ProcedureCallStmt) s.get(0);
			assert(symTable.Get(procStmt.getName()) instanceof RoutineDecl);
			RoutineDecl routine = (RoutineDecl) symTable.Get(procStmt.getName());
			assert(routine.getType() == null);
		});
		analyzers.put(42, (s, self) -> {
			if(s.get(0) instanceof FunctionCallExpn) {
				FunctionCallExpn funcExpn = (FunctionCallExpn) s.get(0);
				assert(funcExpn.getArguments() == null);
			} else if(s.get(0) instanceof ProcedureCallStmt) {
				ProcedureCallStmt procStmt = (ProcedureCallStmt) s.get(0);
				assert(procStmt.getArguments() == null);
			} else {
				assert(false);
			}
		});
		analyzers.put(43, (s, self) -> {
			RoutineDecl routine;
			if(s.get(0) instanceof FunctionCallExpn) {
				FunctionCallExpn funcExpn = (FunctionCallExpn) s.get(0);
				routine = (RoutineDecl) symTable.Get(funcExpn.getIdent());
				assert(routine.getParameters().size()
						!= funcExpn.getArguments().size());
			} else if(s.get(0) instanceof ProcedureCallStmt) {
				ProcedureCallStmt procStmt = (ProcedureCallStmt) s.get(0);
				routine = (RoutineDecl) symTable.Get(procStmt.getName());
				assert(routine.getParameters().size()
						!= procStmt.getArguments().size());
			} else {
				assert(false);
			}
		});
		analyzers.put(44, (s, self) -> {

		});
		analyzers.put(45, (s, self) -> {

		});

		// Custom semantic action -- new loop. Need to create a new context
		analyzers.put(98, (s, self) -> {
			Context newContext = new Context(this.context, ContextType.LOOP);
			this.context = newContext;
		});

		// Custom semantic action -- exit context. Need to switch to previous context
		analyzers.put(99, (s, self) -> {
			this.context = this.context.GetPrev();
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
		if(procStmt.getArguments() != null) {
			semanticAction(44, procStmt);
		}
	}
	@Override
	public void visitLeave(ProcedureCallStmt procStmt) {
		if(procStmt.getArguments() != null) {
			semanticAction(43, procStmt);
		} else {
			semanticAction(42, procStmt);
		}
	}

	@Override
	public void visitEnter(WhileDoStmt whileStmt) {
		if(whileStmt.getExpn() != null) {
			semanticAction(30, whileStmt.getExpn());
		}
		semanticAction(98);
	}
	@Override
	public void visitLeave(WhileDoStmt whileStmt) {
		semanticAction(99);
	}

	@Override
	public void visitEnter(RepeatUntilStmt repeatStmt) {
		semanticAction(98);
	}
	@Override
	public void visitLeave(RepeatUntilStmt repeatStmt) {
		if(repeatStmt.getExpn() != null) {
			semanticAction(30, repeatStmt.getExpn());
		}
		semanticAction(99);
	}
	@Override
	public void visitEnter(ScopeStmt scopeStmt) {
		semanticAction(6);
	}
	@Override
	public void visitLeave(ScopeStmt scopeStmt) {
		semanticAction(7);
	}
	@Override
	public void visitEnter(IfStmt ifStmt) {
		semanticAction(30, ifStmt.getCondition());
	}

	// ===== EXPRESSIONS ===== //
	@Override
	public void visitEnter(ArithExpn arith) {
		semanticAction(31, arith.getLeft());
	}
	@Override
	public void visitLeave(ArithExpn arith) {
		semanticAction(31, arith.getRight());
		semanticAction(21, arith);
	}
	@Override
	public void visitEnter(BoolExpn boolExpn) {
		semanticAction(30, boolExpn.getLeft());
	}
	@Override
	public void visitLeave(BoolExpn boolExpn) {
		semanticAction(30, boolExpn.getRight());
		semanticAction(20, boolExpn);
	}
	@Override
	public void visitEnter(CompareExpn compExpn) {
		semanticAction(31, compExpn.getLeft());
	}
	@Override
	public void visitLeave(CompareExpn compExpn) {
		semanticAction(31, compExpn.getRight());
		semanticAction(20, compExpn);
	}
	@Override
	public void visitEnter(ConditionalExpn condExpn) {
		semanticAction(30, condExpn.getCondition());
	}
	@Override
	public void visitLeave(ConditionalExpn condExpn) {
		semanticAction(33, condExpn.getTrueValue(), condExpn.getFalseValue());
		semanticAction(24, condExpn);
	}
	@Override
	public void visitEnter(EqualsExpn equalExpn) {
		semanticAction(31, equalExpn.getLeft());
	}
	@Override
	public void visitLeave(EqualsExpn equalExpn) {
		semanticAction(31, equalExpn.getLeft());
		semanticAction(21, equalExpn);
	}
	@Override
	public void visitEnter(FunctionCallExpn funcExpn) {
		semanticAction(40, funcExpn);
		if(funcExpn.getArguments() != null) {
			semanticAction(44, funcExpn);
		}
	}
	@Override
	public void visitLeave(FunctionCallExpn funcExpn) {
		if(funcExpn.getArguments() != null) {
			semanticAction(43, funcExpn);
		} else {
			semanticAction(42, funcExpn);
		}
		semanticAction(28, funcExpn);
	}
	@Override
	public void visitLeave(NotExpn notExpn) {
		semanticAction(30, notExpn.getOperand());
		semanticAction(20, notExpn);
	}
	@Override
	public void visitLeave(UnaryMinusExpn minusExpn) {
		semanticAction(31, minusExpn.getOperand());
		semanticAction(21, minusExpn);
	}

	// ===== NON LEAF NODES ===== //

	@Override
	public void visit(ArrayDeclPart arrPart) {
		semanticAction(46, arrPart);
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

	@Override
	public void visit(IdentExpn ident) {
		semanticAction(37, ident);
		semanticAction(26, ident);
	}
	@Override
	public void visit(SubsExpn subs) {
		semanticAction(31, subs.getSubscript1());
		if (subs.getSubscript2() != null) {
			semanticAction(31, subs.getSubscript2());
		}
		semanticAction(27, subs);
	}
	@Override
	public void visit(BoolConstExpn boolExpn) {
		semanticAction(20);
	}
	@Override
	public void visit(IntConstExpn intExpn) {
		semanticAction(21);
	}

	public void EnterScope(List<BaseAST> s)
	{
		symTable.EnterScope();
	}

	public void ExitScope(List<BaseAST> s)
	{
		symTable.ExitScope();
	}

}

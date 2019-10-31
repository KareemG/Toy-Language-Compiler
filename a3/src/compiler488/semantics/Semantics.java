package compiler488.semantics;

import java.io.*;
import java.util.Map;
import java.util.ArrayList;
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
public class Semantics extends ASTVisitor.Default {

	/** flag for tracing semantic analysis */
	private boolean traceSemantics = false;
	/** file sink for semantic analysis trace */
	private String traceFile = new String();
	public FileWriter Tracer;
	public File f;

	private SymbolTable symTable;
	private Context context;
	private Map<Integer, BiConsumer<List<BaseAST>, Semantics>> analyzers;

	private boolean err;

	/** SemanticAnalyzer constructor */
	public Semantics() {
		Initialize();
	}

	public Semantics(String traceFile) {
		Initialize();
		this.traceSemantics = true;
		this.traceFile = traceFile;
	}

	private void printError(String str) {
		System.err.println("ERROR: " + str);
		this.err = true;
	}

	public boolean didError() {
		return this.err;
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
		this.err = false;

		this.symTable.Initialize();

		// Start program scope.
		analyzers.put(0, (s, self) -> {
			assert(s.get(0) instanceof Program);
		});

		// End program scope.
		analyzers.put(1, (s, self) -> {
			assert(s.get(0) instanceof Program);
		});

		// Start function scope.
		analyzers.put(4, (s, self) -> {
			assert(s.get(0) instanceof RoutineDecl);
			RoutineDecl decl = (RoutineDecl) s.get(0);
			symTable.enterScope(decl.getName(), decl.getType());
			this.context = new Context(this.context, ContextType.FUNCTION, decl.getType());
		});

		// End function scope.
		analyzers.put(5, (s, self) -> {
			symTable.exitScope();
		});

		// Start ordinary scope.
		analyzers.put(6, (s, self) -> {
			symTable.enterScope();
		});

		// End ordinary scope.
		analyzers.put(7, (s, self) -> {
			symTable.exitScope();
		});

		// Start procedure scope.
		analyzers.put(8, (s, self) -> {
			assert(s.get(0) instanceof RoutineDecl);
			RoutineDecl decl = (RoutineDecl) s.get(0);
			symTable.enterScope(decl.getName());
			this.context = new Context(this.context, ContextType.PROCEDURE);
		});

		// End procedure scope.
		analyzers.put(9, (s, self) -> {
			symTable.exitScope();
		 });

		// Declare scalar variable.
		analyzers.put(10, (s, self) -> {
			assert(s.get(0) instanceof ScalarDeclPart);
			ScalarDeclPart decl = (ScalarDeclPart) s.get(0);
			Record newRec = new Record(decl.getName(), RecordType.SCALAR);
			if(this.symTable.put(newRec.getIdent(), newRec) == 1)
				printError("Attempt to declare a variable more than once");
		});

		// Declare function with no parameters and specified type.
		analyzers.put(11, (s, self) -> {
			assert(s.get(0) instanceof RoutineDecl);
			RoutineDecl decl = (RoutineDecl) s.get(0);
			Record newRec = new Record(decl.getName(), RecordType.FUNCTION);
			newRec.setResult(decl.getType());
			if(this.symTable.put(newRec.getIdent(), newRec) == 1) // S11
				printError("Attempt to declare a variable more than once");
		});

		// Declare function with parameters and specified type.
		analyzers.put(12, (s, self) -> {
			assert(s.get(0) instanceof RoutineDecl);
			RoutineDecl decl = (RoutineDecl) s.get(0);
			Record newRec = new Record(decl.getName(), RecordType.FUNCTION);
			newRec.setResult(decl.getType());
			newRec.setParam(symTable.getParams());
			if(this.symTable.pPut(newRec.getIdent(), newRec) == 1) // S12
				printError("Attempt to declare a variable more than once");
		});

		// Associate scope with function/procedure.
		analyzers.put(13, (s, self) -> {  });

		// S14, S15, S16 all included
		analyzers.put(14, (s, self) -> {
			assert(s.get(0) instanceof RoutineDecl);
			RoutineDecl decl = (RoutineDecl) s.get(0);
			assert(decl.getParameters() != null);

			symTable.initParams();
			for (ScalarDecl p : decl.getParameters()) {
				Record pRec = new Record(p.getName(), RecordType.PARAMETER);
				pRec.setResult(p.getType());
				symTable.addParams(pRec);
			}
		});

		// Declare parameter with specified type.
		analyzers.put(15, analyzers.get(10));

		// Increment parameter count by one.
		analyzers.put(16, (s, self) -> {
			// TODO(golaubka): y tho? codegen?
		});

		// Declare procedure with no parameters.
		analyzers.put(17, (s, self) -> {
			assert(s.get(0) instanceof RoutineDecl);
			RoutineDecl decl = (RoutineDecl) s.get(0);
			Record newRec = new Record(decl.getName(), RecordType.PROCEDURE);
			if(this.symTable.put(newRec.getIdent(), newRec) == 1)
				printError("Attempt to declare a variable more than once");
		});

		// Declare procedure with parameters.
		analyzers.put(18, (s, self) -> {
			assert(s.get(0) instanceof RoutineDecl);
			RoutineDecl decl = (RoutineDecl) s.get(0);
			Record newRec = new Record(decl.getName(), RecordType.PROCEDURE);
			newRec.setParam(symTable.getParams());
			if(this.symTable.pPut(newRec.getIdent(), newRec) == 1)
				printError("Attempt to declare a variable more than once");
		});

		// Declare array variable with specified lower and upper bounds.
		analyzers.put(19, (s, self) -> {
			assert(s.get(0) instanceof ArrayDeclPart);
			ArrayDeclPart decl = (ArrayDeclPart) s.get(0);
			Record newRec = new Record(decl.getName(), RecordType.ARRAY);
			if(this.symTable.put(decl.getName(), newRec) == 1)
				printError("Attempt to declare a variable more than once");
		});

		// Check that lower bound is <= upper bound.
		analyzers.put(46, (s, self) -> {
			assert(s.get(0) instanceof ArrayDeclPart);

			ArrayDeclPart decl = (ArrayDeclPart) s.get(0);

			if(decl.getLowerBoundary1() > decl.getUpperBoundary1())
				printError("Lower boundary of first index is larger than upper boundary");
			if(decl.isTwoDimensional() &&
					decl.getLowerBoundary2() > decl.getUpperBoundary2())
				printError("Lower boundary of second index is larger than upper boundary");
		});

		// Associate type with variables.
		analyzers.put(47, (s, self) -> {
			assert(s.get(0) instanceof DeclarationPart);
			assert(s.get(1) instanceof Type);
			Type type = (Type) s.get(1);
			DeclarationPart declPart = (DeclarationPart) s.get(0);
			symTable.get(declPart.getName()).setResult(type);
		});

		// ===== EXPRESSION TYPES ===== //
		analyzers.put(20, (s, self) -> {
			((Expn) s.get(0)).setType(new BooleanType());
		});
		analyzers.put(21, (s, self) -> {
			((Expn) s.get(0)).setType(new IntegerType());
		});
		analyzers.put(23, (s, self) -> {});
		analyzers.put(24, (s, self) -> {
			assert(s.get(0) instanceof ConditionalExpn);
			ConditionalExpn condExpn = (ConditionalExpn) s.get(0);
			condExpn.setType(condExpn.getTrueValue().getType());
		});
		analyzers.put(25, (s, self) -> {
			assert(s.get(0) instanceof IdentExpn);
			IdentExpn ident = (IdentExpn) s.get(0);
			if(symTable.get(ident.getName()) == null)
				printError(ident.getName() + " not found");
			ident.setType(symTable.get(ident.getIdent()).getResult());
		});
		analyzers.put(27, (s, self) -> {
			assert(s.get(0) instanceof SubsExpn);
			SubsExpn subsExpn = (SubsExpn) s.get(0);
			if(symTable.get(subsExpn.getVariable()) == null) {
				printError(subsExpn.getName() + " not found");
			}
			subsExpn.setType(symTable.get(subsExpn.getVariable()).getResult());
		});
		analyzers.put(28, (s, self) -> {
			assert(s.get(0) instanceof FunctionCallExpn);
			FunctionCallExpn func = (FunctionCallExpn) s.get(0);
			if(symTable.get(func.getIdent()) == null)
				printError(func.getIdent() + " not found");
			func.setType(symTable.get(func.getIdent()).getResult());
		});

		// ===== EXPRESSION TYPE CHECKING ===== //
		analyzers.put(30, (s, self) -> {
			assert(s.get(0) instanceof Expn);
			Expn expn = (Expn) s.get(0);
			if(!(expn.getType() instanceof BooleanType))
				printError("Expected expression type is boolean but it is not"
					+ "Instead, it is: " + expn.getType());
		});
		analyzers.put(31, (s, self) -> {
			assert(s.get(0) instanceof Expn);
			Expn expn = (Expn) s.get(0);
			if(!(expn.getType() instanceof IntegerType))
				printError("Expected expression type is integer but it is not. "
					+ "Instead, it is: " + expn.getType());
		});
		analyzers.put(32, (s, self) -> {
			assert(s.get(0) instanceof Expn);
			assert(s.get(1) instanceof Expn);
			if(!(((Expn) s.get(0)).getType().getClass().equals(((Expn) s.get(1)).getType().getClass())));
				printError("Expressions type mismatch");
		});
		analyzers.put(33, (s, self) -> {
			// TODO: Redundant perhaps?
			assert(s.get(0) instanceof Expn);
			assert(s.get(1) instanceof Expn);
			if(!(((Expn) s.get(0)).getType().equals(((Expn) s.get(1)).getType())));
				printError("Expressions type mismatch");
		});
		analyzers.put(34, (s, self) -> {
			assert(s.get(0) instanceof ReadableExpn);
			assert(s.get(1) instanceof Expn);
			Record rec = symTable.get(((ReadableExpn)s.get(0)).getName());
			if(!rec.getResult().getClass().equals(((Expn)s.get(1)).getType().getClass()))
				printError("Assignment type mismatch: " + rec.getResult()
					+ " : " + (((ReadableExpn)s.get(0)).getType()));
		});
		analyzers.put(35, (s, self) -> {
			assert(s.get(0) instanceof ReturnStmt);
			Context traverse = this.context;
			while(traverse.GetType() != ContextType.FUNCTION)
				traverse = traverse.GetPrev();
			if(traverse.GetRetType() == null
				|| !(((ReturnStmt)s.get(0)).getValue().getType().getClass().equals(traverse.GetRetType().getClass())))
				printError("Return value type does not match function's return type");
		});
		analyzers.put(36, (s, self) -> {
			// We have also checked the size of the params vs args here (S43)
			Record rec = null;
			Expn []args = null;
			if(s.get(0) instanceof ProcedureCallStmt) {
				ProcedureCallStmt tmp = (ProcedureCallStmt) s.get(0);
				rec = symTable.get(tmp.getName());
				args = tmp.getArguments().toArray(new Expn[tmp.getArguments().size()]);
			} else if (s.get(0) instanceof FunctionCallExpn) {
				FunctionCallExpn tmp = (FunctionCallExpn) s.get(0);
				rec = symTable.get(((FunctionCallExpn)s.get(0)).getIdent());
				args = tmp.getArguments().toArray(new Expn[tmp.getArguments().size()]);
			} else {
				printError("Call to something that is not a function or procedure");
			}
			if(rec == null)
				printError("Call to function/procedure that does not exist");
			else if(rec.getParamCount() != args.length)
				printError("Argument and parameter size mismatch");

			ArrayList<Record> param = rec.getParams();
			for(int i = 0; i < s.size(); i++) {
				assert(s.get(i) instanceof Expn);
				if(!(args[i].getType().getClass().equals(param.get(i).getResult().getClass())))
					printError("Type of parameter and argument mismatch");
			}
		});
		analyzers.put(37, (s, self) -> {
			assert(s.get(0) instanceof ScalarDecl);
			Record rec = symTable.get(((ScalarDecl)s.get(0)).getName());
			if(rec == null || rec.getType() != RecordType.SCALAR)
				printError("Identifier not declared or is not a scalar");
		});
		analyzers.put(38, (s, self) -> {
			assert(s.get(0) instanceof SubsExpn);
			Record rec = symTable.get(((SubsExpn)s.get(0)).getVariable());
			if(rec == null || rec.getType() != RecordType.ARRAY)
				printError("Identifier not declared or is not an array");
		});
		// Probably not necessary. ScalarDecl is only used for parameter, so we
		// can base it off of that
		analyzers.put(39, (s, self) -> {});

		// ===== FUNCTIONS, PROCEDURES AND ARGUMENTS ===== //
		analyzers.put(40, (s, self) -> {
			FunctionCallExpn funcExpn = (FunctionCallExpn) s.get(0);
			Record rec = symTable.get(funcExpn.getIdent());
			if(rec == null || rec.getType() != RecordType.FUNCTION)
				printError("Calling identifier that is not a function");
		});
		analyzers.put(41, (s, self) -> {
			ProcedureCallStmt procStmt = (ProcedureCallStmt) s.get(0);
			Record rec = symTable.get(procStmt.getName());
			if(rec == null || rec.getType() != RecordType.PROCEDURE)
				printError("Calling identifier that is not a procedure");
		});
		analyzers.put(42, (s, self) -> {
			if(s.get(0) instanceof FunctionCallExpn) {
				FunctionCallExpn funcExpn = (FunctionCallExpn) s.get(0);
				if(funcExpn.getArguments() != null)
					printError("Function with parameter called without arguments");
			} else if(s.get(0) instanceof ProcedureCallStmt) {
				ProcedureCallStmt procStmt = (ProcedureCallStmt) s.get(0);
				if(procStmt.getArguments() != null)
					printError("Procedure with parameter called without arguments");
			} else {
				assert(false);
			}
		});
		analyzers.put(43, (s, self) -> {
			Record rec;
			if(s.get(0) instanceof FunctionCallExpn) {
				FunctionCallExpn funcExpn = (FunctionCallExpn) s.get(0);
				rec = symTable.get(funcExpn.getIdent());
				if(rec.getParamCount() != funcExpn.getArguments().size())
					printError("Number of arguments does not equal number of parameters");
			} else if(s.get(0) instanceof ProcedureCallStmt) {
				ProcedureCallStmt procStmt = (ProcedureCallStmt) s.get(0);
				rec = symTable.get(procStmt.getName());
				if (rec.getParamCount() != procStmt.getArguments().size())
					printError("Number of arguments does not equal number of parameters");
			} else {
				printError("Number of arguments does not equal number of parameters");
			}
		});
		analyzers.put(44, (s, self) -> {});
		analyzers.put(45, (s, self) -> {});

		analyzers.put(50, (s, self) -> {
			if(this.context.GetType() != ContextType.LOOP)
				printError("Exit statemetn is not directly inside a loop");
		});
		analyzers.put(51, (s, self) -> {
			Context tmp = this.context;
			while(tmp.GetType() == ContextType.LOOP) {
				tmp = tmp.GetPrev();
			}
			if(tmp.GetType() != ContextType.FUNCTION)
				printError("Return statement is not directly inside a function");
		});
		analyzers.put(52, (s, self) -> {
			Context tmp = this.context;
			while(tmp.GetType() == ContextType.LOOP) {
				tmp = tmp.GetPrev();
			}
			if(tmp.GetType() != ContextType.PROCEDURE)
				printError("Return statement is not directly inside a procedure");
		});
		analyzers.put(53, (s, self) -> {
			int lvl = ((ExitStmt) s.get(0)).getLevel();
			if(lvl <= 0 || lvl > context.GetLoopCount())
				printError("Exit value is greater than number of loops");
		});
		analyzers.put(54, (s, self) -> {
			Context tmp = this.context;
			while(tmp.GetType() != ContextType.FUNCTION) {
				tmp = tmp.GetPrev();
			}
			if(tmp.GetRetCount() > 0) {
				printError("Function does not contain return statement");
			}
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
		this.symTable.Finalize();
	}

	public SymbolTable getSymbolTable() {
		return this.symTable;
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

		//System.out.println("Semantic Action: S" + actionNumber);
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

	// ===== DECLARATIONS ===== //

	@Override
	public void visitEnter(RoutineDecl routine) {
		if(routine.getType() != null) { // Routine is a function
			if(routine.getParameters() != null) {
				semanticAction(4, routine);
				semanticAction(14, routine);
				semanticAction(12, routine);
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
				semanticAction(8, routine);
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
		semanticAction(99);
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
			semanticAction(36, procStmt);
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
		semanticAction(20, equalExpn);
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
			semanticAction(36, funcExpn);
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
	public void visitEnter(SubsExpn subExpn) {
		semanticAction(38, subExpn);
	}
	@Override
	public void visitLeave(SubsExpn subExpn) {
		semanticAction(31, subExpn.getSubscript1());
		if (subExpn.getSubscript2() != null) {
			semanticAction(31, subExpn.getSubscript2());
		}
		semanticAction(27, subExpn);
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
	public void visit(IdentExpn ident) {
		semanticAction(25, ident);
	}
	@Override
	public void visit(BoolConstExpn boolExpn) {
		semanticAction(20, boolExpn);
	}
	@Override
	public void visit(IntConstExpn intExpn) {
		semanticAction(21, intExpn);
	}
}

package compiler488.codegen;

import java.io.*;
import java.util.*;
import compiler488.compiler.Main;
import compiler488.runtime.Machine;
import compiler488.runtime.MemoryAddressException;
import compiler488.ast.*;
import compiler488.ast.decl.*;
import compiler488.ast.expn.*;
import compiler488.ast.stmt.*;
import compiler488.ast.type.*;
import compiler488.semantics.ASTVisitor;
import java.util.function.Consumer;
import java.util.function.BiConsumer;
import compiler488.symbol.*;

/**
 * CodeGenerator.java
 *
 * <pre>
 *  Code Generation Conventions
 *
 *  To simplify the course project, this code generator is
 *  designed to compile directly to pseudo machine memory
 *  which is available as the private array memory[]
 *
 *  It is assumed that the code generator places instructions
 *  in memory in locations
 *
 *      memory[ 0 .. startMSP - 1 ]
 *
 *  The code generator may also place instructions and/or
 *  constants in high memory at locations (though this may
 *  not be necessary)
 *      memory[ startMLP .. Machine.MEMORY_SIZE - 1 ]
 *
 *  During program exection the memory area
 *      memory[ startMSP .. startMLP - 1 ]
 *  is used as a dynamic stack for storing activation records
 *  and temporaries used during expression evaluation.
 *  A hardware exception (stack overflow) occurs if the pointer
 *  for this stack reaches the memory limit register (mlp).
 *
 *  The code generator is responsible for setting the global
 *  variables:
 *      startPC         initial value for program counter
 *      startMSP        initial value for msp
 *      startMLP        initial value for mlp
 * </pre>
 *
 * @author <B> PUT YOUR NAMES HERE </B>
 */

public class CodeGen extends ASTVisitor.Default {
	/** initial value for memory stack pointer */
	private short startMSP;
	/** initial value for program counter */
	private short startPC;
	/** initial value for memory limit pointer */
	private short startMLP;

	/** flag for tracing code generation */
	private boolean traceCodeGen = Main.traceCodeGen;

	private Machine machine;

	private Stack<Short> register_tracker;
	private short current_lexical_level;
	private short routine_counter;
	private SymbolMap map;

	private Stack<IR.Operand> result_stack;
	private ArrayList<IR> intermediate_code;
	private Map<Integer, BiConsumer<List<BaseAST>, CodeGen>> actions;

	/**
	 * Constructor to initialize code generation
	 */
	public CodeGen(Machine machine) {
		this.machine = machine;
		Initialize();
	}

	// Utility procedures used for code generation GO HERE.

	private short new_register()
	{
		Short reg = this.register_tracker.pop();
		this.register_tracker.push((short) (reg + 1));
		return reg;
	}

	private void ir_operation_helper(short operation, IR.Operand lhs, IR.Operand rhs)
	{
		IR.Operand result = new IR.Operand(IR.Operand.REGISTER, this.current_lexical_level, new_register());
		this.intermediate_code.add(new IR(operation, lhs, rhs, new IR.Operand(IR.Operand.REGISTER, result.get_lexical_level(), result.get_value())));
		this.result_stack.push(result);
	}

	/**
	 * Additional intialization for gode generation. Called once at the start of
	 * code generation. May be unnecesary if constructor does everything.
	 */

	/** Additional initialization for Code Generation (if required) */
	void Initialize() {
		/********************************************************/
		/* Initialization code for the code generator GOES HERE */
		/* This procedure is called once before codeGeneration */
		/*                                                      */
		/********************************************************/

		this.actions = new HashMap<>();
		this.intermediate_code = new ArrayList<>();

		this.map = new SymbolMap();
		this.register_tracker = new Stack<Short>();
		this.result_stack = new Stack<IR.Operand>();

		this.routine_counter = 1;
		this.current_lexical_level = 0;
		this.register_tracker.push((short) 0);

		// C00 - Emit code to prepare for the start of program execution.
		actions.put(0, (s, self) -> {
			assert (s.get(0) instanceof Program);
			// this.intermediate_code.add(new IR(IR.SET_DISPLAY, new
			// IR.Operand(IR.Operand.NONE, (short) 0)));
		});

		// C01 - Emit code to end program execution.
		actions.put(1, (s, self) -> {
			assert (s.get(0) instanceof Program);
			this.intermediate_code.add(new IR(IR.HALT));
		});

		// C02 - Set pc, msp and mlp to values for starting program execution.
		actions.put(2, (s, self) -> {
			assert (s.get(0) instanceof Program);

			try {
				this.Finalize();
			} catch (Exception e) {
			}
		});

		// C03 - Emit code (if any) to enter an ordinary scope.
		actions.put(3, (s, self) -> {
			this.map.push(this.current_lexical_level);
		});

		// C04 - Emit code (if any) to exit an ordinary scope.
		actions.put(4, (s, self) -> {
			this.map.pop();
		});

		// C10 - Emit code for the start of a function with no parameters.
		actions.put(10, (s, self) -> {
			assert(s.get(0) instanceof RoutineDecl);

			register_tracker.add((short) 3); // R00 = return value, R01 = return address, R02 = previous display value

			IR.Operand lexical_level = new IR.Operand(IR.Operand.NONE, this.current_lexical_level);
			IR.Operand offset = new IR.Operand(IR.Operand.NONE, (short) 3);
			IR.Operand display_register = new IR.Operand(IR.Operand.REGISTER, this.current_lexical_level, (short) 2);

			this.intermediate_code.add(new IR(IR.UPDATE_DISPLAY, lexical_level, offset,	display_register));
			this.intermediate_code.add(new IR(IR.ALLOC_FRAME, new IR.Operand(IR.Operand.PATCH, (short) 0)));
		});

		// C11 - Emit code for the end of a function with no parameters.
		actions.put(11, (s, self) -> {
			assert(s.get(0) instanceof RoutineDecl);

			short frame_size = (short) (this.register_tracker.pop() - 3);

			this.intermediate_code.add(new IR(IR.ROUTINE_EXIT));
			this.intermediate_code.add(new IR(IR.PATCH_FRAME, new IR.Operand(IR.Operand.NONE, frame_size)));
			this.intermediate_code.add(new IR(IR.FREE_FRAME, new IR.Operand(IR.Operand.NONE, frame_size)));
			this.intermediate_code.add(new IR(IR.RESTORE_DISPLAY, new IR.Operand(IR.Operand.NONE, this.current_lexical_level--)));
			this.intermediate_code.add(new IR(IR.ROUTINE_RETURN));
		});

		// C12 - Emit code for the start of a function with parameters.
		actions.put(12, (s, self) -> {
			assert(s.get(0) instanceof RoutineDecl);

			IR.Operand lexical_level = new IR.Operand(IR.Operand.NONE, this.current_lexical_level);
			IR.Operand offset = new IR.Operand(IR.Operand.NONE, (short) (((RoutineDecl) s.get(0)).getParameters().size() + 3));
			IR.Operand display_register = new IR.Operand(IR.Operand.REGISTER, this.current_lexical_level, (short) 2);

			this.intermediate_code.add(new IR(IR.UPDATE_DISPLAY, lexical_level, offset, display_register));
			this.intermediate_code.add(new IR(IR.ALLOC_FRAME, new IR.Operand(IR.Operand.PATCH, (short) 0)));
		});

		// C13 - Emit code for the end of a function with parameters.
		actions.put(13, (s, self) -> {
			assert(s.get(0) instanceof RoutineDecl);

			short num_registers = this.register_tracker.pop();
			short num_parameters = (short) ((RoutineDecl) s.get(0)).getParameters().size();

			short frame_size = (short) (num_registers - num_parameters - 3);

			this.intermediate_code.add(new IR(IR.ROUTINE_EXIT));
			this.intermediate_code.add(new IR(IR.PATCH_FRAME, new IR.Operand(IR.Operand.NONE, frame_size)));
			this.intermediate_code.add(new IR(IR.FREE_FRAME, new IR.Operand(IR.Operand.NONE, (short) (num_registers - 3))));
			this.intermediate_code.add(new IR(IR.RESTORE_DISPLAY, new IR.Operand(IR.Operand.NONE, this.current_lexical_level--)));
			this.intermediate_code.add(new IR(IR.ROUTINE_RETURN));
		});

		// C14 - Emit code for the start of a procedure with no parameters.
		actions.put(14, (s, self) -> {
			assert(s.get(0) instanceof RoutineDecl);

			register_tracker.add((short) 2); // R00 = return address, R01 = previous display value

			IR.Operand lexical_level = new IR.Operand(IR.Operand.NONE, this.current_lexical_level);
			IR.Operand offset = new IR.Operand(IR.Operand.NONE, (short) 2);
			IR.Operand display_register = new IR.Operand(IR.Operand.REGISTER, this.current_lexical_level, (short) 1);

			this.intermediate_code.add(new IR(IR.UPDATE_DISPLAY, lexical_level, offset,	display_register));
			this.intermediate_code.add(new IR(IR.ALLOC_FRAME, new IR.Operand(IR.Operand.PATCH, (short) 0)));
		});

		// C15 - Emit code for the end of a procedure with no parameters.
		actions.put(15, (s, self) -> {
			assert(s.get(0) instanceof RoutineDecl);

			short frame_size = (short) (this.register_tracker.pop() - 2);

			this.intermediate_code.add(new IR(IR.ROUTINE_EXIT));
			this.intermediate_code.add(new IR(IR.PATCH_FRAME, new IR.Operand(IR.Operand.NONE, frame_size)));
			this.intermediate_code.add(new IR(IR.FREE_FRAME, new IR.Operand(IR.Operand.NONE, frame_size)));
			this.intermediate_code.add(new IR(IR.RESTORE_DISPLAY, new IR.Operand(IR.Operand.NONE, this.current_lexical_level--)));
			this.intermediate_code.add(new IR(IR.ROUTINE_RETURN));
		});

		// C16 - Emit code for the start of a procedure with parameters.
		actions.put(16, (s, self) -> {
			assert(s.get(0) instanceof RoutineDecl);

			IR.Operand lexical_level = new IR.Operand(IR.Operand.NONE, this.current_lexical_level);
			IR.Operand offset = new IR.Operand(IR.Operand.NONE, (short) (((RoutineDecl) s.get(0)).getParameters().size() + 2));
			IR.Operand display_register = new IR.Operand(IR.Operand.REGISTER, this.current_lexical_level, (short) 1);

			this.intermediate_code.add(new IR(IR.UPDATE_DISPLAY, lexical_level, offset, display_register));
			this.intermediate_code.add(new IR(IR.ALLOC_FRAME, new IR.Operand(IR.Operand.PATCH, (short) 0)));
		});

		// C17 - Emit code for the end of a procedure with parameters.
		actions.put(17, (s, self) -> {
			assert(s.get(0) instanceof RoutineDecl);

			short num_registers = this.register_tracker.pop();
			short num_parameters = (short) ((RoutineDecl) s.get(0)).getParameters().size();

			short frame_size = (short) (num_registers - num_parameters - 2);

			this.intermediate_code.add(new IR(IR.ROUTINE_EXIT));
			this.intermediate_code.add(new IR(IR.PATCH_FRAME, new IR.Operand(IR.Operand.NONE, frame_size)));
			this.intermediate_code.add(new IR(IR.FREE_FRAME, new IR.Operand(IR.Operand.NONE, (short) (num_registers - 2))));
			this.intermediate_code.add(new IR(IR.RESTORE_DISPLAY, new IR.Operand(IR.Operand.NONE, this.current_lexical_level--)));
			this.intermediate_code.add(new IR(IR.ROUTINE_RETURN));
		});

		// C18 - Emit code to return from a function.
		actions.put(18, (s, self) -> {
			this.intermediate_code.add(new IR(IR.ASSIGN, new IR.Operand(IR.Operand.REGISTER, this.current_lexical_level, (short) 0), this.result_stack.pop()));
			this.intermediate_code.add(new IR(IR.RETURN));
		});

		// C19 - Emit code to return from a procedure.
		actions.put(19, (s, self) -> {
			this.intermediate_code.add(new IR(IR.RETURN));
		});

		// C20 - Emit any code required before the parameter list of a function.
		actions.put(20, (s, self) -> {
			register_tracker.add((short) 3); // R00 = return value, R01 = return address, R01 = previous display value
		});

		// C21 - Emit any code required after the parameter list of a function.
		actions.put(21, (s, self) -> {
		});

		// C22 - Emit any code required before the parameter list of a procedure.
		actions.put(22, (s, self) -> {
			register_tracker.add((short) 2); // R00 = return address, R01 = previous display value
		});

		// C23 - Emit any code required after the parameter list of a procedure.
		actions.put(23, (s, self) -> {
		});

		// C24 - Emit any code required for a parameter.
		actions.put(24, (s, self) -> {
		});

		// C25 - Emit any code required before a function argument list.
		actions.put(25, (s, self) -> {
			this.intermediate_code.add(new IR(IR.INIT_FUNC_FRAME));
		});

		// C26- Emit any code required after a function argument list.
		actions.put(26, (s, self) -> {
			assert(s.get(0) instanceof FunctionCallExpn);

			FunctionCallExpn expn = (FunctionCallExpn) s.get(0);
			for(int i = 0; i < expn.getArguments().size(); i++)
			{
				this.intermediate_code.add(new IR(IR.COPY, this.result_stack.pop()));
			}
		});

		// C27 - Emit any code required before a procedure argument list.
		actions.put(27, (s, self) -> {
			assert(s.get(0) instanceof ProcedureCallStmt);
			this.intermediate_code.add(new IR(IR.INIT_PROC_FRAME));
		});

		// C28 - Emit any code required after a procedure argument list.
		actions.put(28, (s, self) -> {
			assert(s.get(0) instanceof ProcedureCallStmt);

			ProcedureCallStmt stmt = (ProcedureCallStmt) s.get(0);
			for(int i = 0; i < stmt.getArguments().size(); i++)
			{
				this.intermediate_code.add(new IR(IR.COPY, this.result_stack.pop()));
			}
		});

		// C29 - Emit any code required for an argument.
		actions.put(29, (s, self) -> {
		});

		// C30 - Allocate storage for a scalar variable. Save address in symbol table.
		actions.put(30, (s, self) -> {
			assert (s.get(0) instanceof ScalarDeclPart);
			ScalarDeclPart decl = (ScalarDeclPart) s.get(0);
			this.map.insert(decl.getName(), new SymbolMap.Scalar(new_register()));
		});

		// C31 - Allocate storage for a 1 dimensional array variable. Save address in
		// symbol table.
		actions.put(31, (s, self) -> {
			assert(s.get(0) instanceof ArrayDeclPart);
			assert(((ArrayDeclPart) s.get(0)).isTwoDimensional() == false);

			ArrayDeclPart decl = ((ArrayDeclPart) s.get(0));

			// allocate registers for all the elements
			Short offset = this.register_tracker.pop();
			this.register_tracker.push((short) (offset + decl.getSize()));

			this.map.insert(decl.getName(), new SymbolMap.Array1D(offset, decl.getLowerBoundary1().shortValue()));
		});

		// C32 - Allocate storage for a parameter. Save address in symbol table.
		actions.put(32, (s, self) -> {
			assert(s.get(0) instanceof ScalarDecl);
			ScalarDecl decl = (ScalarDecl) s.get(0);
			this.map.insert(decl.getName(), new SymbolMap.Scalar(new_register()));
		});

		// C33 - Allocate storage for the return value of a function. Save address in
		// symbol table.
		actions.put(33, (s, self) -> {
		});

		// C34 - Save entry point address of procedure or function in symbol table.
		actions.put(34, (s, self) -> {
			assert(s.get(0) instanceof RoutineDecl);

			short id = this.routine_counter++;
			RoutineDecl decl = (RoutineDecl) s.get(0);

			this.map.insert(decl.getName(), (decl.getType() != null) ? new SymbolMap.Function(id) : new SymbolMap.Procedure(id));
			this.map.push(++this.current_lexical_level);

			this.intermediate_code.add(new IR(IR.ROUTINE_ENTRY, new IR.Operand(IR.Operand.NONE, id)));
		});

		// C35 - Emit a forward branch around a function or procedure body.
		actions.put(35, (s, self) -> {
			this.intermediate_code.add(new IR(IR.BR, new IR.Operand(IR.Operand.PATCH, (short) 0)));
		});

		// C36 - Fill in address of forward branch generated by C35.
		actions.put(36, (s, self) -> {
			this.intermediate_code.add(new IR(IR.PATCH_BR));
			this.map.pop();
		});

		// C37 - Allocate storage for a 2 dimensional array variable. Save address in
		// symbol table.
		actions.put(37, (s, self) -> {
			assert(s.get(0) instanceof ArrayDeclPart);
			assert(((ArrayDeclPart) s.get(0)).isTwoDimensional() == true);

			ArrayDeclPart decl = ((ArrayDeclPart) s.get(0));

			// allocate registers for all the elements
			Short offset = this.register_tracker.pop();
			this.register_tracker.push((short) (offset + decl.getSize()));

			short offset1 = decl.getLowerBoundary1().shortValue();
			short offset2 = decl.getLowerBoundary2().shortValue();
			short stride  = (short) (decl.getUpperBoundary1() - decl.getLowerBoundary1() + 1);

			this.map.insert(decl.getName(), new SymbolMap.Array2D(offset, offset1, offset2, stride));
		});

		// C40 - Emit unconditional branch. Save address of branch instruction.
		actions.put(40, (s, self) -> {
			assert (s.get(0) instanceof IfStmt);
			this.intermediate_code.add(new IR(IR.BR, new IR.Operand(IR.Operand.PATCH, (short) 0)));
		});

		// C41 - Fill in address of branch instruction generated by C40.
		actions.put(41, (s, self) -> {
			assert (s.get(0) instanceof IfStmt);
			this.intermediate_code.add(new IR(IR.PATCH_BR));
		});

		// C42 - Emit branch on FALSE. Save address of branch instruction.
		actions.put(42, (s, self) -> {
			IR.Operand condition = this.result_stack.pop();
			this.intermediate_code.add(new IR(IR.BF, condition, new IR.Operand(IR.Operand.PATCH, (short) 0)));
		});

		// C43 - Fill in address of branch instruction generated by C42.
		actions.put(43, (s, self) -> {
			this.intermediate_code.add(new IR(IR.PATCH_BF));
		});

		// C44 - Emit branch on FALSE to address saved by C46.
		actions.put(44, (s, self) -> {
			IR.Operand condition = this.result_stack.pop();
			this.intermediate_code.add(new IR(IR.COND_REPEAT, condition));
		});

		// C45 - Fill in address of branch instructions, if any, generated by C48 and
		// C57 in the appropriate loop.
		actions.put(45, (s, self) -> {
			this.intermediate_code.add(new IR(IR.PATCH_EXIT_LIST));
		});

		// C46 - Save current code address for backward branch.
		actions.put(46, (s, self) -> {
			assert (s.get(0) instanceof LoopingStmt);
			this.intermediate_code.add(new IR(IR.LOOP_START));
		});

		// C47 - Emit branch to address saved by C46.
		actions.put(47, (s, self) -> {
			assert (s.get(0) instanceof LoopingStmt);
			this.intermediate_code.add(new IR(IR.REPEAT));
		});

		// C48 - Emit unconditional branch. Save address of branch instruction. Save
		// level if any.
		actions.put(48, (s, self) -> {
			assert (s.get(0) instanceof ExitStmt);

			ExitStmt stmt = (ExitStmt) s.get(0);
			short level = stmt.getLevel() <= 1 ? 1 : stmt.getLevel().shortValue();
			this.intermediate_code.add(new IR(IR.EXIT, new IR.Operand(IR.Operand.NONE, (short) 0, level)));
		});

		// C49 - Emit code to call a function with no arguments.
		actions.put(49, (s, self) -> {
			assert(s.get(0) instanceof IdentExpn);
			SymbolMap.Function entry = (SymbolMap.Function) this.map.search(((IdentExpn) s.get(0)).getName());
			
			this.intermediate_code.add(new IR(IR.INIT_FUNC_FRAME));

			IR.Operand result = new IR.Operand(IR.Operand.REGISTER, this.current_lexical_level, new_register());
			this.intermediate_code.add(new IR(IR.CALL_ROUTINE, new IR.Operand(IR.Operand.NONE, entry.location), result));
			this.result_stack.add(result);
		});

		// C50 - Emit code to call a function with arguments.
		actions.put(50, (s, self) -> {
			assert(s.get(0) instanceof FunctionCallExpn);
			SymbolMap.Function entry = (SymbolMap.Function) this.map.search(((FunctionCallExpn) s.get(0)).getIdent());

			IR.Operand result = new IR.Operand(IR.Operand.REGISTER, this.current_lexical_level, new_register());
			this.intermediate_code.add(new IR(IR.CALL_ROUTINE, new IR.Operand(IR.Operand.NONE, entry.location), result));
			this.result_stack.add(result);
		});

		// C51 - Emit code to print an integer expression.
		actions.put(51, (s, self) -> {
			assert (s.get(0) instanceof PrintExpn);
			IR.Operand result = result_stack.pop();
			this.intermediate_code.add(new IR(IR.PRINTI, result));
		});

		// C52 - Emit code to print a text string.
		actions.put(52, (s, self) -> {
			assert (s.get(0) instanceof TextConstExpn);
			for (char ch : ((TextConstExpn) s.get(0)).getValue().toCharArray()) {
				this.intermediate_code.add(new IR(IR.PRINTC, new IR.Operand(IR.Operand.NONE, (short) 0, (short) ch)));
			}
		});

		// C53 - Emit code to implement newline.
		actions.put(53, (s, self) -> {
			assert (s.get(0) instanceof SkipConstExpn);
			this.intermediate_code.add(new IR(IR.PRINTC, new IR.Operand(IR.Operand.NONE, (short) 0, (short) '\n')));
		});

		// C54 - Emit code to read one integer value and save it in a variable.
		actions.put(54, (s, self) -> {
			this.intermediate_code.add(new IR(IR.READI, this.result_stack.pop()));
		});

		// C55 - Emit code to call a procedure with no arguments.
		actions.put(55, (s, self) -> {
			assert(s.get(0) instanceof ProcedureCallStmt);
			SymbolMap.Procedure entry = (SymbolMap.Procedure) this.map.search(((ProcedureCallStmt) s.get(0)).getName());
			this.intermediate_code.add(new IR(IR.INIT_PROC_FRAME));
			this.intermediate_code.add(new IR(IR.CALL_ROUTINE, new IR.Operand(IR.Operand.NONE, entry.location)));
		});

		// C56 - Emit code to call a procedure with arguments.
		actions.put(56, (s, self) -> {
			assert(s.get(0) instanceof ProcedureCallStmt);
			SymbolMap.Procedure entry = (SymbolMap.Procedure) this.map.search(((ProcedureCallStmt) s.get(0)).getName());
			this.intermediate_code.add(new IR(IR.CALL_ROUTINE, new IR.Operand(IR.Operand.NONE, entry.location)));
		});

		// C57 - Emit branch on TRUE. Save address of branch instruction. Save level if
		// any.
		actions.put(57, (s, self) -> {
			assert (s.get(0) instanceof ExitStmt);

			ExitStmt stmt = (ExitStmt) s.get(0);
			short level = stmt.getLevel() <= 1 ? 1 : stmt.getLevel().shortValue();
			IR.Operand cond = result_stack.pop();
			this.intermediate_code.add(new IR(IR.COND_EXIT, new IR.Operand(IR.Operand.NONE, (short) 0, level), cond));
		});

		// C60 - Emit instruction(s) to perform negation.
		actions.put(60, (s, self) -> {
			assert (s.get(0) instanceof UnaryMinusExpn);
			IR.Operand operand = result_stack.pop();
			IR.Operand result = new IR.Operand(IR.Operand.REGISTER, this.current_lexical_level, new_register());
			this.intermediate_code.add(new IR(IR.NEG, operand, result));
			this.result_stack.push(result);
		});

		// C61 - Emit instruction(s) to perform addition.
		actions.put(61, (s, self) -> {
			assert (s.get(0) instanceof ArithExpn && ((ArithExpn) s.get(0)).getOpSymbol().equals(ArithExpn.OP_PLUS));
			IR.Operand rhs = result_stack.pop();
			IR.Operand lhs = result_stack.pop();
			ir_operation_helper(IR.ADD, lhs, rhs);
		});

		// C62 - Emit instruction(s) to perform subtraction.
		actions.put(62, (s, self) -> {
			assert (s.get(0) instanceof ArithExpn && ((ArithExpn) s.get(0)).getOpSymbol().equals(ArithExpn.OP_MINUS));
			IR.Operand rhs = result_stack.pop();
			IR.Operand lhs = result_stack.pop();
			ir_operation_helper(IR.SUB, lhs, rhs);
		});

		// C63 - Emit instruction(s) to perform multiplication.
		actions.put(63, (s, self) -> {
			assert (s.get(0) instanceof ArithExpn && ((ArithExpn) s.get(0)).getOpSymbol().equals(ArithExpn.OP_TIMES));
			IR.Operand rhs = result_stack.pop();
			IR.Operand lhs = result_stack.pop();
			ir_operation_helper(IR.MUL, lhs, rhs);
		});

		// C64 - Emit instruction(s) to perform division.
		actions.put(64, (s, self) -> {
			assert (s.get(0) instanceof ArithExpn && ((ArithExpn) s.get(0)).getOpSymbol().equals(ArithExpn.OP_DIVIDE));
			IR.Operand rhs = result_stack.pop();
			IR.Operand lhs = result_stack.pop();
			ir_operation_helper(IR.DIV, lhs, rhs);
		});

		// C65 - Emit instruction(s) to perform logical not operation.
		actions.put(65, (s, self) -> {
			assert (s.get(0) instanceof NotExpn);
			IR.Operand operand = result_stack.pop();
			IR.Operand result = new IR.Operand(IR.Operand.REGISTER, this.current_lexical_level, new_register());
			this.intermediate_code.add(new IR(IR.NOT, operand, result));
			this.result_stack.add(result);
		});

		// C66 - Emit instruction(s) to perform logical and operation.
		actions.put(66, (s, self) -> {
			assert (s.get(0) instanceof BoolExpn && ((BoolExpn) s.get(0)).getOpSymbol().equals(BoolExpn.OP_AND));
			IR.Operand rhs = result_stack.pop();
			IR.Operand lhs = result_stack.pop();
			ir_operation_helper(IR.AND, lhs, rhs);
		});

		// C67 - Emit instruction(s) to perform logical or operation.
		actions.put(67, (s, self) -> {
			assert (s.get(0) instanceof BoolExpn && ((BoolExpn) s.get(0)).getOpSymbol().equals(BoolExpn.OP_OR));
			IR.Operand rhs = result_stack.pop();
			IR.Operand lhs = result_stack.pop();
			ir_operation_helper(IR.OR, lhs, rhs);
		});

		// C68 - Emit instruction(s) to obtain address of parameter.
		actions.put(68, (s, self) -> {
		});

		// C69 - Emit instruction(s) to perform equality comparison.
		actions.put(69, (s, self) -> {
			assert (s.get(0) instanceof EqualsExpn
					&& ((EqualsExpn) s.get(0)).getOpSymbol().equals(EqualsExpn.OP_EQUAL));
			IR.Operand rhs = result_stack.pop();
			IR.Operand lhs = result_stack.pop();
			ir_operation_helper(IR.EQ, lhs, rhs);
		});

		// C70 - Emit instruction(s) to perform inequality comparison.
		actions.put(70, (s, self) -> {
			assert (s.get(0) instanceof EqualsExpn
					&& ((EqualsExpn) s.get(0)).getOpSymbol().equals(EqualsExpn.OP_NOT_EQUAL));
			IR.Operand rhs = result_stack.pop();
			IR.Operand lhs = result_stack.pop();
			ir_operation_helper(IR.NEQ, lhs, rhs);
		});

		// C71 - Emit instruction(s) to perform less than comparison.
		actions.put(71, (s, self) -> {
			assert (s.get(0) instanceof CompareExpn
					&& ((CompareExpn) s.get(0)).getOpSymbol().equals(CompareExpn.OP_LESS));
			IR.Operand rhs = result_stack.pop();
			IR.Operand lhs = result_stack.pop();
			ir_operation_helper(IR.LT, lhs, rhs);
		});

		// C72 - Emit instruction(s) to perform less than or equal comparison.
		actions.put(72, (s, self) -> {
			assert (s.get(0) instanceof CompareExpn
					&& ((CompareExpn) s.get(0)).getOpSymbol().equals(CompareExpn.OP_LESS_EQUAL));
			IR.Operand rhs = result_stack.pop();
			IR.Operand lhs = result_stack.pop();
			ir_operation_helper(IR.LEQ, lhs, rhs);
		});

		// C73 - Emit instruction(s) to perform greater than comparison.
		actions.put(73, (s, self) -> {
			assert (s.get(0) instanceof CompareExpn
					&& ((CompareExpn) s.get(0)).getOpSymbol().equals(CompareExpn.OP_GREATER));
			IR.Operand rhs = result_stack.pop();
			IR.Operand lhs = result_stack.pop();
			ir_operation_helper(IR.GT, lhs, rhs);
		});

		// C74 - Emit instruction(s) to perform greater than or equal comparison.
		actions.put(74, (s, self) -> {
			assert (s.get(0) instanceof CompareExpn
					&& ((CompareExpn) s.get(0)).getOpSymbol().equals(CompareExpn.OP_GREATER_EQUAL));
			IR.Operand rhs = result_stack.pop();
			IR.Operand lhs = result_stack.pop();
			ir_operation_helper(IR.GEQ, lhs, rhs);
		});

		// C75 - Emit instruction(s) to obtain address of variable.
		actions.put(75, (s, self) -> {
		});

		// C76 - Emit instruction(s) to obtain value of variable or parameter.
		actions.put(76, (s, self) -> {
			assert (s.get(0) instanceof IdentExpn);

			SymbolMap.Entry entry = this.map.search(((IdentExpn) s.get(0)).getName());

			if(entry.type == SymbolMap.Entry.TYPE.SCALAR) {
				this.result_stack.add(new IR.Operand(IR.Operand.REGISTER, entry.parent.lexical_level, ((SymbolMap.Scalar) entry).register));
			} else {
				System.out.println("error: invalid symbol type");
			}
		});

		// C77 - Emit instruction(s) to store a value in a variable.
		actions.put(77, (s, self) -> {
			assert (result_stack.size() == 2);
			IR.Operand rhs = result_stack.pop();
			IR.Operand lhs = result_stack.pop();
			this.intermediate_code.add(new IR(IR.ASSIGN, lhs, rhs));
		});

		// C78 - Emit instruction(s) to load the value MACHINEFALSE.
		actions.put(78, (s, self) -> {
			assert (s.get(0) instanceof BoolConstExpn);
			IR.Operand result = new IR.Operand(IR.Operand.REGISTER, this.current_lexical_level, new_register());
			this.intermediate_code.add(new IR(IR.ASSIGN, result, new IR.Operand(IR.Operand.NONE, Machine.MACHINE_FALSE)));
			this.result_stack.add(result);
		});

		// C79 - Emit instruction(s) to load the value MACHINETRUE.
		actions.put(79, (s, self) -> {
			assert (s.get(0) instanceof BoolConstExpn);
			IR.Operand result = new IR.Operand(IR.Operand.REGISTER, this.current_lexical_level, new_register());
			this.intermediate_code.add(new IR(IR.ASSIGN, result, new IR.Operand(IR.Operand.NONE, Machine.MACHINE_TRUE)));
			this.result_stack.add(result);
		});

		// C80 - Emit instruction(s) to load the value of the integer constant.
		actions.put(80, (s, self) -> {
			assert (s.get(0) instanceof IntConstExpn);
			IR.Operand result = new IR.Operand(IR.Operand.REGISTER, this.current_lexical_level, new_register());
			this.intermediate_code.add(new IR(IR.ASSIGN, result, new IR.Operand(IR.Operand.NONE, ((IntConstExpn) s.get(0)).getValue().shortValue())));
			this.result_stack.add(result);
		});

		// C81 - Emit instructions(s)to obtain address of an array variable.
		actions.put(81, (s, self) -> {
			assert(s.get(0) instanceof SubsExpn);

			short register = 0;
			SymbolMap.Entry entry = this.map.search(((SubsExpn) s.get(0)).getVariable());

			if(entry.type == SymbolMap.Entry.TYPE.ARRAY_1D) {
				register = ((SymbolMap.Array1D) entry).base_register;
			} else if(entry.type == SymbolMap.Entry.TYPE.ARRAY_2D) {
				register = ((SymbolMap.Array2D) entry).base_register;
			}

			IR.Operand result = new IR.Operand(IR.Operand.REGISTER, this.current_lexical_level, new_register());
			this.intermediate_code.add(new IR(IR.ADDRESS, new IR.Operand(IR.Operand.NONE, entry.parent.lexical_level),
				new IR.Operand(IR.Operand.NONE, register), result));
			this.result_stack.add(result);
		});

		// C82 - Emit instruction(s) to create address of a 1 dimensional array element.
		actions.put(82, (s, self) -> {
			assert(s.get(0) instanceof SubsExpn);
			assert(((SubsExpn) s.get(0)).getSubscript2() == null);

			SymbolMap.Array1D array = (SymbolMap.Array1D) this.map.search(((SubsExpn) s.get(0)).getVariable());

			IR.Operand index = this.result_stack.pop();
			IR.Operand base = this.result_stack.pop();

			IR.Operand final_index = new IR.Operand(IR.Operand.REGISTER, this.current_lexical_level, new_register());
			IR.Operand result = new IR.Operand(IR.Operand.PTR, this.current_lexical_level, new_register());

			this.intermediate_code.add(new IR(IR.ASSIGN, final_index, index));
			if(array.offset != 0) {
				this.intermediate_code.add(new IR(IR.SUB, final_index, new IR.Operand(IR.Operand.NONE, array.offset), final_index));
			}

			this.intermediate_code.add(new IR(IR.INDEX, base, final_index, result));
			this.result_stack.add(result);
		});

		// C86 - Emit instruction(s) to create address of a 2 dimensional array element.
		actions.put(86, (s, self) -> {
			assert(s.get(0) instanceof SubsExpn);
			assert(((SubsExpn) s.get(0)).getSubscript2() != null);

			SymbolMap.Array2D array = (SymbolMap.Array2D) this.map.search(((SubsExpn) s.get(0)).getVariable());

			IR.Operand index2 = this.result_stack.pop();
			IR.Operand index1 = this.result_stack.pop();
			IR.Operand base = this.result_stack.pop();

			IR.Operand final_index = new IR.Operand(IR.Operand.REGISTER, this.current_lexical_level, new_register());
			IR.Operand result = new IR.Operand(IR.Operand.PTR, this.current_lexical_level, new_register());

			this.intermediate_code.add(new IR(IR.ASSIGN, final_index, index2));
			if(array.offset2 != 0) {
				this.intermediate_code.add(new IR(IR.SUB, final_index, new IR.Operand(IR.Operand.NONE, array.offset2), final_index));
			}
			if(array.stride != 0) {
				this.intermediate_code.add(new IR(IR.MUL, final_index, new IR.Operand(IR.Operand.NONE, array.stride), final_index));
			}

			this.intermediate_code.add(new IR(IR.ADD, final_index, index1, final_index));
			if(array.offset1 != 0) {
				this.intermediate_code.add(new IR(IR.SUB, final_index, new IR.Operand(IR.Operand.NONE, array.offset1), final_index));
			}

			this.intermediate_code.add(new IR(IR.INDEX, base, final_index, result));
			this.result_stack.add(result);
		});

		// C90 - Custom action to get result of a ternary expression
		actions.put(90, (s, self) -> {
			IR.Operand f_value = this.result_stack.pop();
			IR.Operand t_value = this.result_stack.pop();
			IR.Operand condition = this.result_stack.pop();
			IR.Operand result = new IR.Operand(IR.Operand.REGISTER, this.current_lexical_level, new_register());
			this.intermediate_code.add(new IR(IR.COND_ASSIGN, condition, result, t_value, f_value));
			this.result_stack.add(result);
		});
	}

	/**
	 * Perform any required cleanup at the end of code generation. Called once at
	 * the end of code generation.
	 *
	 * @throws MemoryAddressException from Machine.writeMemory
	 */
	void Finalize() throws MemoryAddressException {
		/********************************************************/
		/* Finalization code for the code generator GOES HERE. */
		/*                                                      */
		/* This procedure is called once at the end of code */
		/* generation */
		/********************************************************/

		// REPLACE THIS CODE WITH YOUR OWN CODE
		// THIS CODE generates a single HALT instruction
		// as an example.
		// machine.setPC((short) 0); /* where code to be executed begins */
		// machine.setMSP((short) 1); /* where memory stack begins */
		// machine.setMLP((short) (Machine.MEMORY_SIZE - 1));
		/* limit of stack */
		// machine.writeMemory((short) 0, Machine.HALT);

		// insert initialization code here:

		// convert the IR code into machine code
		try {
			Translator translator = new Translator(this.machine, this.intermediate_code);
			translator.initialize(this.register_tracker.peek());
			this.startMSP = translator.translate();
		} catch (Exception e) {
			System.out.println("error generating machine code from intermediate code:");
			e.printStackTrace();
		}

		machine.setPC((short) 0); /* where code to be executed begins */
		machine.setMSP(this.startMSP); /* where memory stack begins */
		machine.setMLP((short) (Machine.MEMORY_SIZE - 1));
	}

	/**
	 * Procedure to implement code generation based on code generation action number
	 *
	 * @param actionNumber code generation action to perform
	 */
	void generateCode(int actionNumber, BaseAST... nodes) {
		if (traceCodeGen) {
			// output the standard trace stream
			Main.traceStream.println("CodeGen: C" + actionNumber);
		}

		/****************************************************************/
		/* Code to implement the code generation actions GOES HERE */
		/* This dummy code generator just prints the actionNumber */
		/* In Assignment 5, you'll implement something more interesting */
		/*                                                               */
		/* FEEL FREE TO ignore or replace this procedure */
		/****************************************************************/

		System.out.println("Codegen: C" + actionNumber);

		this.actions.get(actionNumber).accept(Arrays.asList(nodes), this);
	}

	@Override
	public void visitEnter(Program prog) {
		generateCode(0, prog);
	}

	@Override
	public void visitLeave(Program prog) {
		generateCode(1, prog);
		generateCode(2, prog);
	}

	@Override
	public void visit(PrintExpn expn) {
		generateCode(51, expn);
	}

	@Override
	public void visit(SkipConstExpn expn) {
		generateCode(53, expn);
	}

	@Override
	public void visit(TextConstExpn expn) {
		generateCode(52, expn);
	}

	@Override
	public void visit(ScalarDeclPart decl) {
		generateCode(30, decl);
	}

	@Override
	public void visitLeave(AssignStmt assign) {
		generateCode(77, assign);
	}

	@Override
	public void visit(IdentExpn expn)
	{
		SymbolMap.Entry entry = this.map.search(expn.getName());

		if(entry.type == SymbolMap.Entry.TYPE.FUNCTION) {
			generateCode(49, expn);
		} else {
			generateCode(76, expn);
		}
	}

	@Override
	public void visitEnter(FunctionCallExpn expn) {
		generateCode(25, expn);
	}

	@Override
	public void visitLeave(FunctionCallExpn expn) {
		generateCode(26, expn);
		generateCode(50, expn);
	}

	@Override
	public void visit(IntConstExpn expn) {
		generateCode(80, expn);
	}

	@Override
	public void visit(BoolConstExpn expn) {
		generateCode(expn.getValue() ? 79 : 78, expn);
	}

	@Override
	public void visitEnter(IfStmt stmt) {
		generateCode(42, stmt);
	}

	@Override
	public void visit(IfStmt stmt) {
		if (stmt.getWhenFalse() != null) {
			generateCode(40);
		}

		generateCode(43);
	}

	@Override
	public void visitLeave(IfStmt stmt) {
		if (stmt.getWhenFalse() != null) {
			generateCode(41, stmt);
		}
	}

	@Override
	public void visitEnter(WhileDoStmt stmt) {
		generateCode(46, stmt);
	}

	@Override
	public void visit(WhileDoStmt stmt) {
		generateCode(42, stmt);
	}

	@Override
	public void visitLeave(WhileDoStmt stmt) {
		generateCode(47, stmt);
		generateCode(43, stmt);
		generateCode(45, stmt);
	}

	@Override
	public void visitEnter(RepeatUntilStmt stmt) {
		generateCode(46, stmt);
	}

	@Override
	public void visitLeave(RepeatUntilStmt stmt) {
		generateCode(44, stmt);
		generateCode(45, stmt);
	}

	@Override
	public void visit(ExitStmt stmt) {
		generateCode(stmt.getExpn() == null ? 48 : 57, stmt);
	}

	@Override
	public void visitLeave(UnaryMinusExpn expn) {
		generateCode(60, expn);
	}

	@Override
	public void visitLeave(ArithExpn expn) {
		switch (expn.getOpSymbol()) {
		case ArithExpn.OP_PLUS:
			generateCode(61, expn);
			break;
		case ArithExpn.OP_MINUS:
			generateCode(62, expn);
			break;
		case ArithExpn.OP_TIMES:
			generateCode(63, expn);
			break;
		case ArithExpn.OP_DIVIDE:
			generateCode(64, expn);
			break;
		}
	}

	@Override
	public void visitLeave(NotExpn expn) {
		generateCode(65, expn);
	}

	@Override
	public void visitLeave(BoolExpn expn) {
		if (expn.getOpSymbol().equals(BoolExpn.OP_AND)) {
			generateCode(66, expn);
		} else {
			generateCode(67, expn);
		}
	}

	@Override
	public void visitLeave(EqualsExpn expn) {
		if (expn.getOpSymbol().equals(EqualsExpn.OP_EQUAL)) {
			generateCode(69, expn);
		} else {
			generateCode(70, expn);
		}
	}

	@Override
	public void visitLeave(CompareExpn expn) {
		switch (expn.getOpSymbol()) {
		case CompareExpn.OP_LESS:
			generateCode(71, expn);
			break;
		case CompareExpn.OP_LESS_EQUAL:
			generateCode(72, expn);
			break;
		case CompareExpn.OP_GREATER:
			generateCode(73, expn);
			break;
		case CompareExpn.OP_GREATER_EQUAL:
			generateCode(74, expn);
			break;
		}
	}

	@Override
	public void visit(ArrayDeclPart array)
	{
		if(!array.isTwoDimensional()) {
			generateCode(31, array);
		} else {
			generateCode(37, array);
		}
	}

	@Override
	public void visitEnter(SubsExpn expn)
	{
		generateCode(81, expn);
	}

	@Override
	public void visitLeave(SubsExpn expn)
	{
		if(expn.getSubscript2() == null) {
			generateCode(82, expn);
		} else {
			generateCode(86, expn);
		}
	}

	@Override
	public void visitEnter(ConditionalExpn expn)
	{
		// generateCode(42, expn);
	}

	@Override
	public void visit(ConditionalExpn expn)
	{
		// generateCode(40, expn);
		// generateCode(43, expn);
	}

	@Override
	public void visitLeave(ConditionalExpn expn)
	{
		// generateCode(41, expn);
		generateCode(90, expn);
	}

	@Override
	public void visitEnter(RoutineDecl routine)
	{
		generateCode(35, routine);
		generateCode(34, routine);

		if(routine.getType() == null)
		{
			if(routine.getParameters().size() > 0) {
				generateCode(22, routine);
			} else {
				generateCode(14, routine);
			}
		}
		else
		{
			if(routine.getParameters().size() > 0) {
				generateCode(20, routine);
			} else {
				generateCode(10, routine);
			}
		}
	}

	@Override
	public void visit(RoutineDecl routine)
	{
		if(routine.getParameters().size() > 0) {
			if(routine.getType() == null)
			{
				generateCode(23, routine);
				generateCode(16, routine);
			}
			else
			{
				generateCode(21, routine);
				generateCode(12, routine);
			}
		}
	}

	@Override
	public void visitLeave(RoutineDecl routine)
	{
		if(routine.getType() == null)
		{
			if(routine.getParameters().size() > 0) {
				generateCode(17, routine);
			} else {
				generateCode(15, routine);
			}
		}
		else
		{
			if(routine.getParameters().size() > 0) {
				generateCode(13, routine);
			} else {
				generateCode(11, routine);
			}
		}

		generateCode(36, routine);
	}

	@Override
	public void visitEnter(ProcedureCallStmt stmt)
	{
		if((stmt.getArguments() != null) && (stmt.getArguments().size() > 0))
		{
			generateCode(27, stmt);
		}
	}

	@Override
	public void visitLeave(ProcedureCallStmt stmt)
	{
		if((stmt.getArguments() != null) && (stmt.getArguments().size() > 0))
		{
			generateCode(28, stmt);
			generateCode(56, stmt);
		}
		else
		{
			generateCode(55, stmt);
		}
	}

	@Override
	public void visitLeave(ScalarDecl decl)
	{
		generateCode(32, decl);
	}

	@Override
	public void visit(ReturnStmt stmt)
	{
		if(stmt.getValue() == null) {
			generateCode(19, stmt);
		} else {
			generateCode(18, stmt);
		}
	}

	@Override
	public void visitLeave(ReadStmt stmt)
	{
		ASTList<ReadableExpn> inputs = stmt.getInputs();
		ListIterator<ReadableExpn> it = inputs.listIterator();

		while (it.hasNext())
		{
			ReadableExpn expn = it.next();
			expn.accept(this);

			generateCode(54, expn);
		}
	}

	@Override
	public void visitEnter(ScopeStmt stmt)
	{
		generateCode(3, stmt);
	}

	@Override
	public void visitLeave(ScopeStmt stmt)
	{
		generateCode(4, stmt);
	}
}

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

	// global scope information:
	// these things should be in symbol table but I am doing this for initial
	// testing
	private int reg_offset;
	private HashMap<String, Integer> var_to_reg_map;
	private Stack<Integer> result_stack;

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

	void writeMemory(short addr, short value) {
		try {
			machine.writeMemory(addr, value);
		} catch (Exception e) {
			System.out.println("Error writing instruction");
		}
	}

	void appendCode(short value) {
		this.writeMemory(this.startMSP++, value);
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

		this.reg_offset = 0;
		this.var_to_reg_map = new HashMap<>();
		this.result_stack = new Stack<>();

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
		});

		// C04 - Emit code (if any) to exit an ordinary scope.
		actions.put(4, (s, self) -> {
		});

		// C10 - Emit code for the start of a function with no parameters.
		actions.put(10, (s, self) -> {
		});

		// C11 - Emit code for the end of a function with no parameters.
		actions.put(11, (s, self) -> {
		});

		// C12 - Emit code for the start of a function with parameters.
		actions.put(12, (s, self) -> {
		});

		// C13 - Emit code for the end of a function with parameters.
		actions.put(13, (s, self) -> {
		});

		// C14 - Emit code for the start of a procedure with no parameters.
		actions.put(14, (s, self) -> {
		});

		// C15 - Emit code for the end of a procedure with no parameters.
		actions.put(15, (s, self) -> {
		});

		// C16 - Emit code for the start of a procedure with parameters.
		actions.put(16, (s, self) -> {
		});

		// C17 - Emit code for the end of a procedure with parameters.
		actions.put(17, (s, self) -> {
		});

		// C18 - Emit code to return from a function.
		actions.put(18, (s, self) -> {
		});

		// C19 - Emit code to return from a procedure.
		actions.put(19, (s, self) -> {
		});

		// C20 - Emit any code required before the parameter list of a function.
		actions.put(20, (s, self) -> {
		});

		// C21 - Emit any code required after the parameter list of a function.
		actions.put(21, (s, self) -> {
		});

		// C22 - Emit any code required before the parameter list of a procedure.
		actions.put(22, (s, self) -> {
		});

		// C23 - Emit any code required after the parameter list of a procedure.
		actions.put(23, (s, self) -> {
		});

		// C24 - Emit any code required for a parameter.
		actions.put(24, (s, self) -> {
		});

		// C25 - Emit any code required before a function argument list.
		actions.put(25, (s, self) -> {
		});

		// C26- Emit any code required after a function argument list.
		actions.put(26, (s, self) -> {
		});

		// C27 - Emit any code required before a procedure argument list.
		actions.put(27, (s, self) -> {
		});

		// C28 - Emit any code required after a procedure argument list.
		actions.put(28, (s, self) -> {
		});

		// C29 - Emit any code required for an argument.
		actions.put(29, (s, self) -> {
		});

		// C30 - Allocate storage for a scalar variable. Save address in symbol table.
		actions.put(30, (s, self) -> {
			assert (s.get(0) instanceof ScalarDeclPart);
			ScalarDeclPart decl = (ScalarDeclPart) s.get(0);
			this.var_to_reg_map.put(decl.getName(), this.reg_offset++);
		});

		// C31 - Allocate storage for a 1 dimensional array variable. Save address in
		// symbol table.
		actions.put(31, (s, self) -> {
		});

		// C32 - Allocate storage for a parameter. Save address in symbol table.
		actions.put(32, (s, self) -> {
		});

		// C33 - Allocate storage for the return value of a function. Save address in
		// symbol table.
		actions.put(33, (s, self) -> {
		});

		// C34 - Save entry point address of procedure or function in symbol table.
		actions.put(34, (s, self) -> {
		});

		// C35 - Emit a forward branch around a function or procedure body.
		actions.put(35, (s, self) -> {
		});

		// C36 - Fill in address of forward branch generated by C35.
		actions.put(36, (s, self) -> {
		});

		// C37 - Allocate storage for a 2 dimensional array variable. Save address in
		// symbol table.
		actions.put(37, (s, self) -> {
		});

		// C40 - Emit unconditional branch. Save address of branch instruction.
		actions.put(40, (s, self) -> {
			assert (s.get(0) instanceof IfStmt);
			this.intermediate_code.add(new IR(IR.BR, new IR.Operand(IR.Operand.PATCH_TRUE, (short) 0)));
		});

		// C41 - Fill in address of branch instruction generated by C40.
		actions.put(41, (s, self) -> {
			assert (s.get(0) instanceof IfStmt);
			this.intermediate_code.add(new IR(IR.PATCH_TRUE));
		});

		// C42 - Emit branch on FALSE. Save address of branch instruction.
		actions.put(42, (s, self) -> {
			this.intermediate_code
					.add(new IR(IR.BF, new IR.Operand(IR.Operand.REGISTER, this.result_stack.pop().shortValue()),
							new IR.Operand(IR.Operand.PATCH_FALSE, (short) 0)));
		});

		// C43 - Fill in address of branch instruction generated by C42.
		actions.put(43, (s, self) -> {
			this.intermediate_code.add(new IR(IR.PATCH_FALSE));
		});

		// C44 - Emit branch on FALSE to address saved by C46.
		actions.put(44, (s, self) -> {
			this.intermediate_code
					.add(new IR(IR.COND_REPEAT, new IR.Operand(IR.Operand.NONE, result_stack.pop().shortValue())));
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
			this.intermediate_code.add(new IR(IR.EXIT, new IR.Operand(IR.Operand.NONE, level)));
		});

		// C49 - Emit code to call a function with no arguments.
		actions.put(49, (s, self) -> {
		});

		// C50 - Emit code to call a function with arguments.
		actions.put(50, (s, self) -> {
		});

		// C51 - Emit code to print an integer expression.
		actions.put(51, (s, self) -> {
			assert (s.get(0) instanceof PrintExpn);
			writeMemory(startMSP++, Machine.PRINTI);
		});

		// C52 - Emit code to print a text string.
		actions.put(52, (s, self) -> {
			assert (s.get(0) instanceof TextConstExpn);
			for (char ch : ((TextConstExpn) s.get(0)).getValue().toCharArray()) {
				this.intermediate_code.add(new IR(IR.PRINTC, new IR.Operand(IR.Operand.NONE, (short) ch)));
			}
		});

		// C53 - Emit code to implement newline.
		actions.put(53, (s, self) -> {
			assert (s.get(0) instanceof SkipConstExpn);
			this.intermediate_code.add(new IR(IR.PRINTC, new IR.Operand(IR.Operand.NONE, (short) '\n')));
		});

		// C54 - Emit code to read one integer value and save it in a variable.
		actions.put(54, (s, self) -> {
		});

		// C55 - Emit code to call a procedure with no arguments.
		actions.put(55, (s, self) -> {
		});

		// C56 - Emit code to call a procedure with arguments.
		actions.put(56, (s, self) -> {
		});

		// C57 - Emit branch on TRUE. Save address of branch instruction. Save level if
		// any.
		actions.put(57, (s, self) -> {
			assert (s.get(0) instanceof ExitStmt);

			ExitStmt stmt = (ExitStmt) s.get(0);
			short level = stmt.getLevel() <= 1 ? 1 : stmt.getLevel().shortValue();
			this.intermediate_code.add(new IR(IR.COND_EXIT, new IR.Operand(IR.Operand.NONE, level),
					new IR.Operand(IR.Operand.NONE, result_stack.pop().shortValue())));
		});

		// C60 - Emit instruction(s) to perform negation.
		actions.put(60, (s, self) -> {
			assert (s.get(0) instanceof UnaryMinusExpn);
			int operand = result_stack.pop();
			this.intermediate_code.add(new IR(IR.NEG, new IR.Operand(IR.Operand.REGISTER, (short) operand)));
		});

		// C61 - Emit instruction(s) to perform addition.
		actions.put(61, (s, self) -> {
			assert (s.get(0) instanceof  ArithExpn && ((ArithExpn) s.get(0)).getOpSymbol().equals(ArithExpn.OP_PLUS));
			int rhs = result_stack.pop();
			int lhs = result_stack.pop();
			this.intermediate_code.add(new IR(IR.ADD, new IR.Operand(IR.Operand.REGISTER, (short) lhs), new IR.Operand(IR.Operand.REGISTER, (short) rhs)));
		});

		// C62 - Emit instruction(s) to perform subtraction.
		actions.put(62, (s, self) -> {
			assert (s.get(0) instanceof  ArithExpn && ((ArithExpn) s.get(0)).getOpSymbol().equals(ArithExpn.OP_MINUS));
			int rhs = result_stack.pop();
			int lhs = result_stack.pop();
			this.intermediate_code.add(new IR(IR.SUB, new IR.Operand(IR.Operand.REGISTER, (short) lhs), new IR.Operand(IR.Operand.REGISTER, (short) rhs)));
		});

		// C63 - Emit instruction(s) to perform multiplication.
		actions.put(63, (s, self) -> {
			assert (s.get(0) instanceof  ArithExpn && ((ArithExpn) s.get(0)).getOpSymbol().equals(ArithExpn.OP_TIMES));
			int rhs = result_stack.pop();
			int lhs = result_stack.pop();
			this.intermediate_code.add(new IR(IR.MUL, new IR.Operand(IR.Operand.REGISTER, (short) lhs), new IR.Operand(IR.Operand.REGISTER, (short) rhs)));
		});

		// C64 - Emit instruction(s) to perform division.
		actions.put(64, (s, self) -> {
			assert (s.get(0) instanceof  ArithExpn && ((ArithExpn) s.get(0)).getOpSymbol().equals(ArithExpn.OP_DIVIDE));
			int rhs = result_stack.pop();
			int lhs = result_stack.pop();
			this.intermediate_code.add(new IR(IR.DIV, new IR.Operand(IR.Operand.REGISTER, (short) lhs), new IR.Operand(IR.Operand.REGISTER, (short) rhs)));
		});

		// C65 - Emit instruction(s) to perform logical not operation.
		actions.put(65, (s, self) -> {
			assert (s.get(0) instanceof NotExpn);
			int operand = result_stack.pop();
			this.intermediate_code.add(new IR(IR.NOT, new IR.Operand(IR.Operand.REGISTER, (short) operand)));
		});

		// C66 - Emit instruction(s) to perform logical and operation.
		actions.put(66, (s, self) -> {
			assert (s.get(0) instanceof BoolExpn && ((BoolExpn) s.get(0)).getOpSymbol().equals(BoolExpn.OP_AND));
			int rhs = result_stack.pop();
			int lhs = result_stack.pop();
			this.intermediate_code.add(new IR(IR.AND, new IR.Operand(IR.Operand.REGISTER, (short) lhs), new IR.Operand(IR.Operand.REGISTER, (short) rhs)));
		});

		// C67 - Emit instruction(s) to perform logical or operation.
		actions.put(67, (s, self) -> {
			assert (s.get(0) instanceof BoolExpn && ((BoolExpn) s.get(0)).getOpSymbol().equals(BoolExpn.OP_OR));
			int rhs = result_stack.pop();
			int lhs = result_stack.pop();
			this.intermediate_code.add(new IR(IR.OR, new IR.Operand(IR.Operand.REGISTER, (short) lhs), new IR.Operand(IR.Operand.REGISTER, (short) rhs)));
		});

		// C68 - Emit instruction(s) to obtain address of parameter.
		actions.put(68, (s, self) -> {
		});

		// C69 - Emit instruction(s) to perform equality comparison.
		actions.put(69, (s, self) -> {
			assert (s.get(0) instanceof EqualsExpn && ((EqualsExpn) s.get(0)).getOpSymbol().equals(EqualsExpn.OP_EQUAL));
			int rhs = result_stack.pop();
			int lhs = result_stack.pop();
			this.intermediate_code.add(new IR(IR.EQ, new IR.Operand(IR.Operand.REGISTER, (short) lhs), new IR.Operand(IR.Operand.REGISTER, (short) rhs)));
		});

		// C70 - Emit instruction(s) to perform inequality comparison.
		actions.put(70, (s, self) -> {
			assert (s.get(0) instanceof EqualsExpn && ((EqualsExpn) s.get(0)).getOpSymbol().equals(EqualsExpn.OP_NOT_EQUAL));
			int rhs = result_stack.pop();
			int lhs = result_stack.pop();
			this.intermediate_code.add(new IR(IR.EQ, new IR.Operand(IR.Operand.REGISTER, (short) lhs), new IR.Operand(IR.Operand.REGISTER, (short) rhs)));
			int target_register = this.reg_offset++;
			this.intermediate_code.add(new IR(IR.ASSIGN, new IR.Operand(IR.Operand.REGISTER, (short) target_register),
					new IR.Operand(IR.Operand.NONE, Machine.MACHINE_FALSE)));
			int operand = result_stack.pop();
			this.intermediate_code.add(new IR(IR.EQ, new IR.Operand(IR.Operand.REGISTER, (short) operand), new IR.Operand(IR.Operand.REGISTER, (short) target_register)));
		});

		// C71 - Emit instruction(s) to perform less than comparison.
		actions.put(71, (s, self) -> {
			assert (s.get(0) instanceof CompareExpn && ((CompareExpn) s.get(0)).getOpSymbol().equals(CompareExpn.OP_LESS));
			int rhs = result_stack.pop();
			int lhs = result_stack.pop();
			this.intermediate_code.add(new IR(IR.LT, new IR.Operand(IR.Operand.REGISTER, (short) lhs), new IR.Operand(IR.Operand.REGISTER, (short) rhs)));
		});

		// C72 - Emit instruction(s) to perform less than or equal comparison.
		actions.put(72, (s, self) -> {
			assert (s.get(0) instanceof CompareExpn && ((CompareExpn) s.get(0)).getOpSymbol().equals(CompareExpn.OP_LESS_EQUAL));
			int rhs = result_stack.pop();
			int lhs = result_stack.pop();
			this.intermediate_code.add(new IR(IR.LEQ, new IR.Operand(IR.Operand.REGISTER, (short) lhs), new IR.Operand(IR.Operand.REGISTER, (short) rhs)));
		});

		// C73 - Emit instruction(s) to perform greater than comparison.
		actions.put(73, (s, self) -> {
			assert (s.get(0) instanceof CompareExpn && ((CompareExpn) s.get(0)).getOpSymbol().equals(CompareExpn.OP_GREATER));
			int rhs = result_stack.pop();
			int lhs = result_stack.pop();
			this.intermediate_code.add(new IR(IR.GT, new IR.Operand(IR.Operand.REGISTER, (short) lhs), new IR.Operand(IR.Operand.REGISTER, (short) rhs)));
		});

		// C74 - Emit instruction(s) to perform greater than or equal comparison.
		actions.put(74, (s, self) -> {
			assert (s.get(0) instanceof CompareExpn && ((CompareExpn) s.get(0)).getOpSymbol().equals(CompareExpn.OP_GREATER_EQUAL));
			int rhs = result_stack.pop();
			int lhs = result_stack.pop();
			this.intermediate_code.add(new IR(IR.GEQ, new IR.Operand(IR.Operand.REGISTER, (short) lhs), new IR.Operand(IR.Operand.REGISTER, (short) rhs)));
		});

		// C75 - Emit instruction(s) to obtain address of variable.
		actions.put(75, (s, self) -> {
		});

		// C76 - Emit instruction(s) to obtain value of variable or parameter.
		actions.put(76, (s, self) -> {
			assert (s.get(0) instanceof IdentExpn);
			IdentExpn e = (IdentExpn) s.get(0);
			this.result_stack.add(this.var_to_reg_map.get(e.getName()));
		});

		// C77 - Emit instruction(s) to store a value in a variable.
		actions.put(77, (s, self) -> {
			assert (result_stack.size() == 2);
			int rhs = result_stack.pop();
			int lhs = result_stack.pop();
			this.intermediate_code.add(new IR(IR.ASSIGN, new IR.Operand(IR.Operand.REGISTER, (short) lhs),
					new IR.Operand(IR.Operand.REGISTER, (short) rhs)));
		});

		// C78 - Emit instruction(s) to load the value MACHINEFALSE.
		actions.put(78, (s, self) -> {
			assert (s.get(0) instanceof BoolConstExpn);
			int target_register = this.reg_offset++;
			this.intermediate_code.add(new IR(IR.ASSIGN, new IR.Operand(IR.Operand.REGISTER, (short) target_register),
					new IR.Operand(IR.Operand.NONE, Machine.MACHINE_FALSE)));
			this.result_stack.add(target_register);
		});

		// C79 - Emit instruction(s) to load the value MACHINETRUE.
		actions.put(79, (s, self) -> {
			assert (s.get(0) instanceof BoolConstExpn);
			int target_register = this.reg_offset++;
			this.intermediate_code.add(new IR(IR.ASSIGN, new IR.Operand(IR.Operand.REGISTER, (short) target_register),
					new IR.Operand(IR.Operand.NONE, Machine.MACHINE_TRUE)));
			this.result_stack.add(target_register);
		});

		// C80 - Emit instruction(s) to load the value of the integer constant.
		actions.put(80, (s, self) -> {
			assert (s.get(0) instanceof IntConstExpn);
			int target_register = this.reg_offset++;
			this.intermediate_code.add(new IR(IR.ASSIGN, new IR.Operand(IR.Operand.REGISTER, (short) target_register),
					new IR.Operand(IR.Operand.NONE, ((IntConstExpn) s.get(0)).getValue().shortValue())));
			this.result_stack.add(target_register);
		});

		// C81 - Emit instructions(s)to obtain address of an array variable.
		actions.put(81, (s, self) -> {
		});

		// C82 - Emit instruction(s) to create address of a 1 dimensional array element.
		actions.put(82, (s, self) -> {
		});

		// C86 - Emit instruction(s) to create address of a 2 dimensional array element.
		actions.put(86, (s, self) -> {
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

		Stack<Short> true_stack = new Stack<>();
		Stack<Short> false_stack = new Stack<>();
		Stack<Short> loop_start_addr = new Stack<>();
		Stack<ArrayList<Short>> exit_stack = new Stack<>();

		Consumer<IR> insert_branch_address = (ir) -> {
			writeMemory(this.startMSP, ir.op1.get_value()); // write the default value to the lcoation

			// insert the pointer to the branch location if required:
			IR.Operand opcode = ir.opcode == IR.BR ? ir.op1 : ir.op2;
			if (opcode.needs_true_patch()) {
				true_stack.add(this.startMSP);
			} else if (opcode.needs_false_patch()) {
				false_stack.add(this.startMSP);
			}

			this.startMSP++;
		};

		// insert initialization code here:
		appendCode(Machine.PUSHMT);
		appendCode(Machine.SETD);
		appendCode((short) 0);

		// allocating space for all registers:
		appendCode(Machine.PUSH); // we will be filling the register space with zeroes
		appendCode((short) 0);
		appendCode(Machine.PUSH); // we have 'reg_offset' registers
		appendCode((short) this.reg_offset);
		appendCode(Machine.DUPN); // perform the fill

		// convert the IR code into machine code
		try {
			for (IR ir : this.intermediate_code) {
				switch (ir.opcode) {
				case IR.SET_DISPLAY: {
					appendCode(Machine.PUSHMT);
					appendCode(Machine.SETD);
					appendCode(ir.op1.get_value());
					break;
				}

				case IR.PRINTC: {
					appendCode(Machine.PUSH);
					appendCode(ir.op1.get_value());
					appendCode(Machine.PRINTC);
					break;
				}

				case IR.HALT: {
					appendCode(Machine.HALT);
					break;
				}

				case IR.ASSIGN: {
					if (ir.op1.is_register() && ir.op2.is_register()) {
						// load address of lhs
						appendCode(Machine.ADDR);
						appendCode((short) 0);
						appendCode(ir.op1.get_value());

						// load value of rhs
						appendCode(Machine.ADDR);
						appendCode((short) 0);
						appendCode(ir.op2.get_value());
						appendCode(Machine.LOAD);

						// lhs = rhs
						appendCode(Machine.STORE);
					} else // assuming lhs is the register, and rhs is a constant
					{
						// load address of lhs
						appendCode(Machine.ADDR);
						appendCode((short) 0);
						appendCode(ir.op1.get_value());

						// push constant value to stack
						appendCode(Machine.PUSH);
						appendCode(ir.op2.get_value());

						// lhs = constant
						appendCode(Machine.STORE);
					}
					break;
				}

				case IR.BR: {
					appendCode(Machine.PUSH);
					insert_branch_address.accept(ir);
					appendCode(Machine.BR);
					break;
				}

				case IR.BT:
				case IR.BF: {
					appendCode(Machine.ADDR);
					appendCode((short) 0);
					appendCode(ir.op1.get_value());
					appendCode(Machine.LOAD);

					if (ir.opcode == IR.BT) {
						appendCode(Machine.PUSH);
						appendCode(Machine.MACHINE_TRUE);
						appendCode(Machine.EQ);
					}

					appendCode(Machine.PUSH);
					insert_branch_address.accept(ir);

					appendCode(Machine.BF);
					break;
				}

				case IR.PATCH_FALSE: {
					writeMemory(false_stack.pop(), this.startMSP);
					break;
				}

				case IR.PATCH_TRUE: {
					writeMemory(true_stack.pop(), this.startMSP);
					break;
				}

				case IR.LOOP_START: {
					loop_start_addr.add(this.startMSP);
					exit_stack.add(new ArrayList<Short>());
					break;
				}

				case IR.REPEAT: {
					appendCode(Machine.PUSH);
					appendCode(loop_start_addr.pop());
					appendCode(Machine.BR);
					break;
				}

				case IR.COND_REPEAT: {
					appendCode(Machine.ADDR);
					appendCode((short) 0);
					appendCode(ir.op1.get_value());
					appendCode(Machine.LOAD);

					appendCode(Machine.PUSH);
					appendCode(loop_start_addr.pop());
					appendCode(Machine.BF);
					break;
				}

				case IR.EXIT: {
					appendCode(Machine.PUSH);

					short addr = this.startMSP++; // address which will be patched later on
					writeMemory(addr, (short) 0);
					exit_stack.get(exit_stack.size() - ir.op1.get_value()).add(addr);

					appendCode(Machine.BR);
					break;
				}

				case IR.COND_EXIT: {
					// load the expression result
					appendCode(Machine.ADDR);
					appendCode((short) 0);
					appendCode(ir.op2.get_value());
					appendCode(Machine.LOAD);

					// invert the result for branch-if-true
					appendCode(Machine.PUSH);
					appendCode(Machine.MACHINE_FALSE);
					appendCode(Machine.EQ);

					// push the address to branch out of the loop
					appendCode(Machine.PUSH);

					short addr = this.startMSP++; // address which will be patched later on
					writeMemory(addr, (short) 0);
					exit_stack.get(exit_stack.size() - ir.op1.get_value()).add(addr);

					appendCode(Machine.BF);
					break;
				}

				case IR.PATCH_EXIT_LIST: {
					ArrayList<Short> addresses = exit_stack.pop();
					for (Short addr : addresses) {
						writeMemory(addr, this.startMSP);
					}
					break;
				}

				default: {
					System.out.println("error: unknown intermediate instruction\n");
					throw new Exception();
				}
				}
			}
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
	public void visit(IdentExpn expn) {
		generateCode(76, expn);
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
		switch(expn.getOpSymbol()) {
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
		}
		else {
			generateCode(67, expn);
		}
	}

	@Override
	public void visitLeave(EqualsExpn expn) {
		if (expn.getOpSymbol().equals(EqualsExpn.OP_EQUAL)) {
			generateCode(69, expn);
		}
		else {
			generateCode(70, expn);
		}
	}

	@Override
	public void visitLeave(CompareExpn expn) {
		switch(expn.getOpSymbol()) {
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
}

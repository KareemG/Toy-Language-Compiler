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
import java.util.function.BiConsumer;

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

public class CodeGen extends ASTVisitor.Default
{
	/** initial value for memory stack pointer */
	private short startMSP;
	/** initial value for program counter */
	private short startPC;
	/** initial value for memory limit pointer */
	private short startMLP;

	private Map<Integer, BiConsumer<List<BaseAST>, CodeGen>> actions;

	/** flag for tracing code generation */
	private boolean traceCodeGen = Main.traceCodeGen;

	private Machine machine;

	/**
	 * Constructor to initialize code generation
	 */
	public CodeGen(Machine machine) {
		this.machine = machine;
		Initialize();
	}

	// Utility procedures used for code generation GO HERE.

	void writeMemory(short addr, short value)
	{
		try {
			machine.writeMemory(addr, value);
		} catch(Exception e) {
			System.out.println("Error writing instruction");
		}
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

		// C00 - Emit code to prepare for the start of program execution.
		actions.put(0, (s, self) -> {
			assert(s.get(0) instanceof Program);
			writeMemory(startMSP++, Machine.PUSHMT);
			writeMemory(startMSP++, Machine.SETD);
			writeMemory(startMSP++, (short) 0);
		});

		// C01 - Emit code to end program execution.
		actions.put(1, (s, self) -> {
			assert(s.get(0) instanceof Program);
			writeMemory(startMSP++, Machine.HALT);
		});

		// C02 - Set pc, msp and mlp to values for starting program execution.
		actions.put(2, (s, self) -> {
			assert(s.get(0) instanceof Program);
			machine.setPC((short) 0);
			machine.setMSP(startMSP);
			machine.setMLP(Machine.MEMORY_SIZE);
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
		});
		
		// C31 - Allocate storage for a 1 dimensional array variable. Save address in symbol table.
		actions.put(31, (s, self) -> {
		});

		// C32 - Allocate storage for a parameter. Save address in symbol table.
		actions.put(32, (s, self) -> {
			assert(s.get(0) instanceof ScalarDecl);
			writeMemory(startMSP++, Machine.PUSH);
			writeMemory(startMSP++, Machine.UNDEFINED);
			// TODO: Need to insert this param into symbol table
		});

		// C33 - Allocate storage for the return value of a function. Save address in symbol table.
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

		// C37 - Allocate storage for a 2 dimensional array variable. Save address in symbol table.
		actions.put(37, (s, self) -> {
		});

		// C40 - Emit unconditional branch. Save address of branch instruction.
		actions.put(40, (s, self) -> {
		});

		// C41 - Fill in address of branch instruction generated by C40. 
		actions.put(41, (s, self) -> {
		});

		// C42 - Emit branch on FALSE. Save address of branch instruction.
		actions.put(42, (s, self) -> {
		});

		// C43 - Fill in address of branch instruction generated by C42.
		actions.put(43, (s, self) -> {
		});

		// C44 - Emit branch on FALSE to address saved by C46.
		actions.put(44, (s, self) -> {
		});

		// C45 - Fill in address of branch instructions, if any, generated by C48 and C57 in the appropriate loop.
		actions.put(45, (s, self) -> {
		});

		// C46 - Save current code address for backward branch.
		actions.put(46, (s, self) -> {
		});

		// C47 - Emit branch to address saved by C46.
		actions.put(47, (s, self) -> {
		});

		// C48 - Emit unconditional branch. Save address of branch instruction. Save level if any.
		actions.put(48, (s, self) -> {
		});

		// C49 - Emit code to call a function with no arguments.
		actions.put(49, (s, self) -> {
		});

		// C50 - Emit code to call a function with arguments.
		actions.put(50, (s, self) -> {
		});

		// C51 - Emit code to print an integer expression.
		actions.put(51, (s, self) -> {
		});

		// C52 - Emit code to print a text string.
		actions.put(52, (s, self) -> {
		});

		// C53 - Emit code to implement newline.
		actions.put(53, (s, self) -> {
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

		// C57 - Emit branch on TRUE. Save address of branch instruction. Save level if any.
		actions.put(57, (s, self) -> {
		});

		// C60 - Emit instruction(s) to perform negation.
		actions.put(60, (s, self) -> {
		});

		// C61 - Emit instruction(s) to perform addition.
		actions.put(61, (s, self) -> {
		});

		// C62 - Emit instruction(s) to perform subtraction.
		actions.put(62, (s, self) -> {
		});

		// C63 - Emit instruction(s) to perform multiplication.
		actions.put(63, (s, self) -> {
		});

		// C64 - Emit instruction(s) to perform division.
		actions.put(64, (s, self) -> {
		});

		// C65 - Emit instruction(s) to perform logical not operation.
		actions.put(65, (s, self) -> {
		});

		// C66 - Emit instruction(s) to perform logical and operation.
		actions.put(66, (s, self) -> {
		});

		// C67 - Emit instruction(s) to perform logical or operation.
		actions.put(67, (s, self) -> {
		});

		// C68 - Emit instruction(s) to obtain address of parameter.
		actions.put(68, (s, self) -> {
		});

		// C69 - Emit instruction(s) to perform equality comparison.
		actions.put(69, (s, self) -> {
		});

		// C70 - Emit instruction(s) to perform inequality comparison.
		actions.put(70, (s, self) -> {
		});

		// C71 - Emit instruction(s) to perform less than comparison.
		actions.put(71, (s, self) -> {
		});

		// C72 - Emit instruction(s) to perform less than or equal comparison.
		actions.put(72, (s, self) -> {
		});

		// C73 - Emit instruction(s) to perform greater than comparison.
		actions.put(73, (s, self) -> {
		});

		// C74 - Emit instruction(s) to perform greater than or equal comparison.
		actions.put(74, (s, self) -> {
		});

		// C75 - Emit instruction(s) to obtain address of variable.
		actions.put(75, (s, self) -> {
		});

		// C76 - Emit instruction(s) to obtain value of variable or parameter.
		actions.put(76, (s, self) -> {
		});

		// C77 - Emit instruction(s) to store a value in a variable.
		actions.put(77, (s, self) -> {
		});

		// C78 - Emit instruction(s) to load the value MACHINEFALSE.
		actions.put(78, (s, self) -> {
		});

		// C79 - Emit instruction(s) to load the value MACHINETRUE.
		actions.put(79, (s, self) -> {
		});

		// C80 - Emit instruction(s) to load the value of the integer constant.
		actions.put(80, (s, self) -> {
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

		// C90 - Custom action for write statements
		actions.put(90, (s, self) -> {
			assert(s.get(0) instanceof Printable);
			Printable p = (Printable) s.get(0);

			if(p instanceof TextConstExpn)
			{
				for (char ch : ((TextConstExpn) p).getValue().toCharArray()) {
					writeMemory(startMSP++, Machine.PUSH);
					writeMemory(startMSP++, (short) ch);
					writeMemory(startMSP++, Machine.PRINTC);
				}
			}
		});
	}

	/**
	 * Perform any required cleanup at the end of code generation. Called once
	 * at the end of code generation.
	 *
	 * @throws MemoryAddressException
	 *             from Machine.writeMemory
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
		machine.setPC((short) 0); /* where code to be executed begins */
		machine.setMSP((short) 1); /* where memory stack begins */
		machine.setMLP((short) (Machine.MEMORY_SIZE - 1));
		/* limit of stack */
		machine.writeMemory((short) 0, Machine.HALT);
	}

	/**
	 * Procedure to implement code generation based on code generation action
	 * number
	 *
	 * @param actionNumber
	 *            code generation action to perform
	 */
	void generateCode(int actionNumber, BaseAST... nodes)
	{
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

	// ===== FUNCTION & PROCEDURE DECLARATIONS ===== //
	@Override
	public void visitEnter(RoutineDecl routine) {
		generateCode(35, routine);
		if(routine.getType() != null) { // Routine is a fucntion
			if(routine.getParameters() != null) {
			} else {
			}
		} else { // Routine is a procedure
			if(routine.getParameters() != null) {
			} else {
			}
		}
	}
	@Override
	public void visit(RoutineDecl routine) {
		generateCode(34, routine);
		if(routine.getType() != null) {
			if(routine.getParameters() != null) {
			} else {
			}
		} else {
			if(routine.getParameters() != null) {
			} else {
			}
		}
	}
	@Override
	public void visitLeave(RoutineDecl routine) {
		if(routine.getType() != null) { // Routine is a fucntion
			if(routine.getParameters() != null) {
				generateCode(13, routine);
			} else {
				generateCode(12, routine);
			}
		} else { // Routine is a procedure
			if(routine.getParameters() != null) {
				generateCode(17, routine);
			} else {
				generateCode(15, routine);
			}
		}
		generateCode(36, routine);
	}

	@Override
	public void visitLeave(ScalarDecl decl) {
		generateCode(32, decl);
		generateCode(24, decl);
	}

	@Override
	public void visitLeave(WriteStmt writeStmt)
	{
		ListIterator<Printable> lst = writeStmt.getOutputs().listIterator();
		while(lst.hasNext()) {
			generateCode(90, (Expn) lst.next());
		}
	}
}

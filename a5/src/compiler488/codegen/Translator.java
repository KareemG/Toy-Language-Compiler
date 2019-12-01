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

import javax.sound.midi.Patch;

import java.util.function.BiConsumer;
import compiler488.symbol.*;

public class Translator
{
    private Machine machine;
    private ArrayList<IR> intermediate_code;

    private short wptr;

    Stack<Short> bt_stack;
    Stack<Short> bf_stack;
    Stack<Short> br_stack;
    Stack<Short> loop_stack;
    Stack<ArrayList<Short>> exit_stack;

    public Translator(Machine machine, ArrayList<IR> intermediate_code)
    {
        this.machine = machine;
        this.intermediate_code = intermediate_code;
        
        this.wptr = 0;
        this.bt_stack = new Stack<>();
        this.bf_stack = new Stack<>();
        this.br_stack = new Stack<>();
		this.loop_stack = new Stack<>();
        this.exit_stack = new Stack<>();
    }

    private void write(short addr, short value)
	{
		try {
			machine.writeMemory(addr, value);
		} catch(Exception e) {
			System.out.println("Error writing instruction");
		}
	}

	private short append(short value)
	{
        short addr = wptr;
        write(wptr++, value);
        return addr;
    }

    public void initialize(short num_registers)
    {
        append(Machine.PUSHMT);
		append(Machine.SETD);
		append((short) 0);

		// allocating space for all registers:
		append(Machine.PUSH); // we will be filling the register space with zeroes
		append((short) 0);
		append(Machine.PUSH); // we have 'reg_offset' registers
		append((short) num_registers);
		append(Machine.DUPN); // perform the fill
    }
    
    public short translate() throws Exception
    {
        for(IR ir : this.intermediate_code)
        {
            switch(ir.opcode)
            {
                case IR.SET_DISPLAY:
                {
                    set_display(ir.op1.get_value());
                    break;
                }

                case IR.PRINTC:
                {
                    printc(ir.op1.get_value());
                    break;
                }

                case IR.PRINTI:
                {
                    printi((short) 0, ir.op1.get_value());
                    break;
                }

                case IR.HALT:
                {
                    halt();
                    break;
                }

                case IR.ASSIGN:
                {
                    addr((short) 0, ir.op1.get_value());
                    if(!ir.op2.is_register()) {
                        push(ir.op2.get_value());
                    } else {
                        load((short) 0, ir.op2.get_value());
                    }
                    store();
                    break;
                }

                case IR.BR:
                {
                    branch(ir.op1.get_value(), ir.op1.needs_patch());
                    break;
                }

                case IR.BT:
                {
                    cond_branch(true, ir.op1.get_value(), ir.op2.get_value(), ir.op2.needs_patch());
                    break;
                }

                case IR.BF:
                {
                    cond_branch(false, ir.op1.get_value(), ir.op2.get_value(), ir.op2.needs_patch());
                    break;
                }

                case IR.PATCH_BF:
                {
                    write(bf_stack.pop(), wptr);
                    break;
                }

                case IR.PATCH_BT:
                {
                    write(bt_stack.pop(), wptr);
                    break;
                }

                case IR.PATCH_BR:
                {
                    write(br_stack.pop(), wptr);
                    break;
                }

                case IR.LOOP_START:
                {
                    loop_stack.add(wptr);
                    exit_stack.add(new ArrayList<Short>());
                    break;
                }

                case IR.REPEAT:
                {
                    repeat();
                    break;
                }

                case IR.COND_REPEAT:
                {
                    cond_repeat(ir.op1.get_value());
                    break;
                }

                case IR.EXIT:
                {
                    exit(ir.op1.get_value());
                    break;
                }

                case IR.COND_EXIT:
                {
                    cond_exit(ir.op1.get_value(), ir.op2.get_value());
                    break;
                }

                case IR.PATCH_EXIT_LIST:
                {
                    ArrayList<Short> addresses = exit_stack.pop();
                    for(Short addr : addresses)
                    {
                        write(addr, wptr);
                    }
                    break;
                }

                default:
                {
                    System.out.println("error: unknown intermediate instruction\n");
                    throw new Exception();
                }
            }
        }

        return this.wptr;
    }

    private void set_display(short ptr)
    {
        append(Machine.PUSHMT);
        append(Machine.SETD);
        append(ptr);
    }

    private void printc(short c)
    {
        append(Machine.PUSH);
        append(c);
        append(Machine.PRINTC);
    }

    private void printi(short level, short reg)
    {
        load(level, reg);
        append(Machine.PRINTI);
    }

    private void halt()
    {
        append(Machine.HALT);
    }

    private void load(short lex_level, short addr)
    {
        append(Machine.ADDR);
        append(lex_level);
        append(addr);
        append(Machine.LOAD);
    }

    private void addr(short lex_level, short addr)
    {
        append(Machine.ADDR);
        append(lex_level);
        append(addr);
    }

    private void store()
    {
        append(Machine.STORE);
    }

    private void push(short c)
    {
        append(Machine.PUSH);
        append(c);
    }

    private void invert()
    {
        append(Machine.PUSH);
        append(Machine.MACHINE_FALSE);
        append(Machine.EQ);
    }

    private void branch(short addr, boolean needs_patch)
    {
        append(Machine.PUSH);
        short ptr = append((short) 0);
        append(Machine.BR);

        if(needs_patch) this.br_stack.add(ptr);
    }

    private void cond_branch(boolean type, short cond, short addr, boolean needs_patch)
    {
        load((short) 0, cond); // load the condition result
        if(type) {
            invert();
        }

        append(Machine.PUSH);
        short ptr = append((short) 0);
        append(Machine.BF);

        if(needs_patch)
        {
            if(type) {
                this.bt_stack.add(ptr);
            } else {
                this.bf_stack.add(ptr);
            }
        }
    }

    private void repeat()
    {
        append(Machine.PUSH);
        append(loop_stack.pop());
        append(Machine.BR);
    }

    private void cond_repeat(short cond)
    {
        append(Machine.ADDR);
        append((short) 0);
        append(cond);
        append(Machine.LOAD);

        append(Machine.PUSH);
        append(loop_stack.pop());
        append(Machine.BF);
    }

    private void exit(short level)
    {
        append(Machine.PUSH);

        short addr = append((short) 0); // address which will be patched later on
        exit_stack.get(exit_stack.size() - level).add(addr);

        append(Machine.BR);
    }

    private void cond_exit(short level, short cond)
    {
        // condition
        load((short) 0, cond);
        invert();

        // branch address
        append(Machine.PUSH);
        short addr = append((short) 0);
        exit_stack.get(exit_stack.size() - level).add(addr);

        append(Machine.BF);
    }
}

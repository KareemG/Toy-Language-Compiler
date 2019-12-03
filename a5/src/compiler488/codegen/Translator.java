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

    private Stack<Short> bt_stack;
    private Stack<Short> bf_stack;
    private Stack<Short> br_stack;
    private Stack<Short> loop_stack;
    private Stack<Short> frame_stack;
    private Stack<ArrayList<Short>> exit_stack;
    private Stack<Short> call_stack;

    private HashMap<Short, Short> routine_map;

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
        this.frame_stack = new Stack<>();
        this.call_stack = new Stack<>();

        this.routine_map = new HashMap<Short, Short>();
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
		append(Machine.UNDEFINED);
		append(Machine.PUSH); // we have 'reg_offset' registers
		append((short) num_registers);
		append(Machine.DUPN); // perform the fill
    }

    public static String print(IR.Operand op)
    {
        String str = "null";
        if(op != null)
        {
            str = "(";

            if(op.is_pointer()) {
                str += "PTR";
            } else if(op.is_register()) {
                str += "REGISTER";
            } else if(op.needs_patch()) {
                str += "PATCH";
            } else {
                str += "NONE";
            }

            str += ", " + op.get_lexical_level() + ", " + op.get_value() + ")";
        }
        
        return str;
    }

    public static void print(IR ir)
    {
        String op = "unknown";
        switch(ir.opcode)
        {
            case IR.NEG: op = "NEG"; break;
            case IR.ADD: op = "ADD"; break;
            case IR.SUB: op = "SUB"; break;
            case IR.MUL: op = "MUL"; break;
            case IR.DIV: op = "DIV"; break;
            case IR.EQ: op = "EQ"; break;
            case IR.NEQ: op = "NEQ"; break;
            case IR.LT: op = "LT"; break;
            case IR.GT: op = "GT"; break;
            case IR.LEQ: op = "LEQ"; break;
            case IR.GEQ: op = "GEQ"; break;
            case IR.AND: op = "AND"; break;
            case IR.OR: op = "OR"; break;
            case IR.NOT: op = "NOT"; break;
            case IR.READI: op = "READI"; break;
            case IR.PRINTI: op = "PRINTI"; break;
            case IR.PRINTC: op = "PRINTC"; break;
            case IR.BR: op = "BR"; break;
            case IR.BT: op = "BT"; break;
            case IR.BF: op = "BF"; break;
            case IR.SET_DISPLAY: op = "SET_DISPLAY"; break;
            case IR.HALT: op = "HALT"; break;
            case IR.ASSIGN: op = "ASSIGN"; break;
            case IR.PATCH_BT: op = "PATCH_BT"; break;
            case IR.PATCH_BF: op = "PATCH_BF"; break;
            case IR.PATCH_BR: op = "PATCH_BR"; break;
            case IR.LOOP_START: op = "LOOP_START"; break;
            case IR.REPEAT: op = "REPEAT"; break;
            case IR.COND_REPEAT: op = "COND_REPEAT"; break;
            case IR.EXIT: op = "EXIT"; break;
            case IR.COND_EXIT: op = "COND_EXIT"; break;
            case IR.PATCH_EXIT_LIST: op = "PATCH_EXIT_LIST"; break;
            case IR.ADDRESS: op = "ADDRESS"; break;
            case IR.INDEX: op = "INDEX"; break;
            case IR.COND_ASSIGN: op = "COND_ASSIGN"; break;
            case IR.ROUTINE_ENTRY: op = "ROUTINE_ENTRY"; break;
            case IR.ALLOC_FRAME: op = "ALLOC_FRAME"; break;
            case IR.PATCH_FRAME: op = "PATCH_FRAME"; break;
            case IR.FREE_FRAME: op = "FREE_FRAME"; break;
            case IR.UPDATE_DISPLAY: op = "UPDATE_DISPLAY"; break;
            case IR.CALL_PROC: op = "CALL_PROC"; break;
            case IR.CALL_FUNC: op = "CALL_FUNC"; break;
            case IR.RET: op = "RET"; break;
            case IR.INIT_FRAME: op = "INIT_FRAME"; break;
            case IR.COPY: op = "COPY"; break;
            default: break;
        }

        System.out.println(String.format("(%s, %s, %s, %s, %s)", op, print(ir.op1), print(ir.op2), print(ir.op3), print(ir.op4)));
    }
    
    public short translate() throws Exception
    {
        for(IR ir : this.intermediate_code)
        {
            print(ir);

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
                    printi(ir.op1);
                    break;
                }

                case IR.HALT:
                {
                    halt();
                    break;
                }

                case IR.ASSIGN:
                {
                    assign(ir.op1, ir.op2);
                    break;
                }

                case IR.BR:
                {
                    branch(ir.op1.get_value(), ir.op1.needs_patch());
                    break;
                }

                case IR.BT:
                {
                    cond_branch(true, ir.op1, ir.op2.get_value(), ir.op2.needs_patch());
                    break;
                }

                case IR.BF:
                {
                    cond_branch(false, ir.op1, ir.op2.get_value(), ir.op2.needs_patch());
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
                    cond_exit(ir.op1.get_value(), ir.op2);
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

                case IR.NEG: // negate a integer
                {
                    addr((short) 0, ir.op2.get_value());
                    load((short) 0, ir.op1.get_value());
                    append(Machine.NEG);
                    append(Machine.STORE);
                    break;
                }

                case IR.NOT: // negate a boolean
                {
                    addr((short) 0, ir.op2.get_value());
                    load((short) 0, ir.op1.get_value());
                    negate();
                    append(Machine.STORE);
                    break;
                }

                case IR.OR:
                {
                    boolean_or(ir.op1, ir.op2, ir.op3);
                    break;
                }

                case IR.AND:
                {
                    boolean_and(ir.op1, ir.op2, ir.op3);
                    break;
                }

                case IR.LT:
                case IR.GT:
                case IR.EQ:
                case IR.LEQ:
                case IR.GEQ:
                case IR.NEQ:
                case IR.ADD:
                case IR.SUB:
                case IR.MUL:
                case IR.DIV:
                {
                    addr(ir.op3.get_lexical_level(), ir.op3.get_value()); // the target *must* be a register
                    switch(ir.opcode)
                    {
                        case IR.LT:  { lt(ir.op1, ir.op2); break; }
                        case IR.GT:  { gt(ir.op1, ir.op2); break; }
                        case IR.EQ:  { eq(ir.op1, ir.op2); break; }
                        case IR.LEQ: { leq(ir.op1, ir.op2); break; }
                        case IR.GEQ: { geq(ir.op1, ir.op2); break; }
                        case IR.NEQ: { neq(ir.op1, ir.op2); break; }
                        case IR.ADD: { add(ir.op1, ir.op2); break; }
                        case IR.SUB: { sub(ir.op1, ir.op2); break; }
                        case IR.MUL: { mul(ir.op1, ir.op2); break; }
                        case IR.DIV: { div(ir.op1, ir.op2); break; }
                    }
                    append(Machine.STORE);
                    break;
                }

                case IR.ADDRESS:
                {
                    addr(ir.op3.get_lexical_level(), ir.op3.get_value());
                    addr(ir.op1.get_value(), ir.op2.get_value());
                    append(Machine.STORE);
                    break;
                }

                case IR.INDEX:
                {
                    addr(ir.op3.get_lexical_level(), ir.op3.get_value());
                    add(ir.op1, ir.op2);
                    append(Machine.STORE);
                    break;
                }

                case IR.COND_ASSIGN:
                {
                    cond_assign(ir.op1, ir.op2, ir.op3, ir.op4);
                    break;
                }
    
                case IR.ROUTINE_ENTRY:
                {
                    this.routine_map.put(ir.op1.get_value(), this.wptr);
                    break;
                }

                case IR.ALLOC_FRAME:
                {
                    append(Machine.PUSH);
                    append(Machine.UNDEFINED);
                    append(Machine.PUSH);
                    short frame_size = append((short) 0);
                    append(Machine.DUPN);

                    this.frame_stack.add(frame_size);
                    break;
                }

                case IR.PATCH_FRAME:
                {
                    write(this.frame_stack.pop(), ir.op1.get_value());
                    break;
                }

                case IR.FREE_FRAME:
                {
                    append(Machine.PUSH);
                    append(ir.op1.get_value());
                    append(Machine.POPN);
                    break;
                }

                case IR.UPDATE_DISPLAY:
                {
                    // new value
                    append(Machine.PUSHMT);
                    
                    append(Machine.PUSH);
                    append(ir.op2.get_value());
                    append(Machine.SUB);

                    // old value
                    append(Machine.ADDR);
                    append(ir.op1.get_value());
                    append((short) 0);

                    // update the display
                    append(Machine.SWAP);
                    append(Machine.SETD);
                    append(ir.op1.get_value());

                    append(Machine.ADDR);
                    append(ir.op3.get_lexical_level());
                    append(ir.op3.get_value());
                    append(Machine.SWAP);
                    append(Machine.STORE);
                    
                    break;
                }

                case IR.CALL_PROC:
                {
                    // call the function
                    append(Machine.PUSH);
                    append(this.routine_map.get(ir.op1.get_value()));
                    append(Machine.BR);
                    
                    // write the return address
                    write(this.call_stack.pop(), this.wptr);
                    break;
                }

                case IR.RET:
                {
                    append(Machine.BR);
                    break;
                }

                case IR.INIT_FRAME:
                {
                    // push the return address (needs to be patched)
                    append(Machine.PUSH);
                    short ret_addr = append((short) 0);

                    // push a placeholder for the display value
                    append(Machine.PUSH);
                    append((short) 0);

                    this.call_stack.add(ret_addr);
                    break;
                }

                case IR.COPY:
                {
                    append(Machine.ADDR);
                    append(ir.op1.get_lexical_level());
                    append(ir.op1.get_value());
                    append(Machine.LOAD);
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

    private void assign(IR.Operand lhs, IR.Operand rhs)
    {
        addr(lhs.get_lexical_level(), lhs.get_value());
        if(lhs.is_pointer()) {
            append(Machine.LOAD);
        }

        load_operand(rhs);
        
        store();
    }

    private void cond_assign(IR.Operand cond, IR.Operand result, IR.Operand t_val, IR.Operand f_val)
    {
        load_operand(cond);
        append(Machine.PUSH);
        short false_branch = append((short) 0);
        append(Machine.BF);

        assign(result, t_val);
        append(Machine.PUSH);
        short true_branch = append((short) 0);
        append(Machine.BR);

        write(false_branch, this.wptr); // patch the false branch
        assign(result, f_val);
        write(true_branch, this.wptr);
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

    private void printi(IR.Operand op)
    {
        load_operand(op);
        append(Machine.PRINTI);
    }

    private void halt()
    {
        append(Machine.HALT);
    }

    private void load(short lex_level, short addr)
    {
        addr(lex_level, addr);
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

    private void negate()
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

    private void cond_branch(boolean type, IR.Operand cond, short addr, boolean needs_patch)
    {
        load_operand(cond); // load the condition result
        if(type) {
            negate();
        }

        append(Machine.PUSH);
        short ptr = append(addr);
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

    private void cond_exit(short level, IR.Operand cond)
    {
        // condition
        load_operand(cond);
        negate();

        // branch address
        append(Machine.PUSH);
        short addr = append((short) 0);
        exit_stack.get(exit_stack.size() - level).add(addr);

        append(Machine.BF);
    }

    private void load_operand(IR.Operand op)
    {
        if(!op.is_register() && !op.is_pointer())
        {
            push(op.get_value());
        }
        else
        {
            load(op.get_lexical_level(), op.get_value());

            if(op.is_pointer()) {
                append(Machine.LOAD);
            }
        }
    }

    private void lt(IR.Operand op1, IR.Operand op2)
    {
        load_operand(op1);
        load_operand(op2);
        append(Machine.LT);
    }

    private void gt(IR.Operand op1, IR.Operand op2)
    {
        load_operand(op2);
        load_operand(op1);
        append(Machine.LT);
    }

    private void eq(IR.Operand op1, IR.Operand op2)
    {
        load_operand(op1);
        load_operand(op2);
        append(Machine.EQ);
    }

    private void geq(IR.Operand op1, IR.Operand op2)
    {
        lt(op1, op2);
        negate();
    }

    private void leq(IR.Operand op1, IR.Operand op2)
    {
        gt(op1, op2);
        negate();
    }

    private void neq(IR.Operand op1, IR.Operand op2)
    {
        eq(op1,  op2);
        negate();
    }

    private void add(IR.Operand op1, IR.Operand op2)
    {
        load_operand(op1);
        load_operand(op2);
        append(Machine.ADD);
    }

    private void sub(IR.Operand op1, IR.Operand op2)
    {
        load_operand(op1);
        load_operand(op2);
        append(Machine.SUB);
    }

    private void mul(IR.Operand op1, IR.Operand op2)
    {
        load_operand(op1);
        load_operand(op2);
        append(Machine.MUL);
    }

    private void div(IR.Operand op1, IR.Operand op2)
    {
        load_operand(op1);
        load_operand(op2);
        append(Machine.DIV);
    }

    private void boolean_or(IR.Operand op1, IR.Operand op2, IR.Operand result)
    {
        addr(result.get_lexical_level(), result.get_value());
        load_operand(op1);
        load_operand(op2);
        append(Machine.OR);
        append(Machine.STORE);
    }

    private void boolean_and(IR.Operand op1, IR.Operand op2, IR.Operand result)
    {
        // using De Morgan's law
        addr(result.get_lexical_level(), result.get_value());

        // not A
        load_operand(op1);
        negate();

        // not B
        load_operand(op2);
        negate();

        append(Machine.OR);
        negate(); // not (not A or not B)

        append(Machine.STORE);
    }
}

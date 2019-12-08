package compiler488.codegen;

import java.util.Arrays;
import java.util.List;

public class IR
{
	public static final short NEG = 1;
	public static final short ADD = 2;
	public static final short SUB = 3;
	public static final short MUL = 4;
	public static final short DIV = 5;
	public static final short EQ = 6;
	public static final short NEQ = 7;
	public static final short LT = 8;
	public static final short GT = 9;
	public static final short LEQ = 10;
	public static final short GEQ = 11;
	//public static final short AND = 12;
	//public static final short OR = 13;
	public static final short NOT = 14;
	public static final short READI = 15;
	public static final short PRINTI = 16;
	public static final short PRINTC = 17;
	public static final short BR = 18;
	public static final short BT = 19;
	public static final short BF = 20;
	public static final short SET_DISPLAY = 21;
	public static final short HALT = 22;
	public static final short ASSIGN = 23;
	public static final short PATCH_BT = 24;
	public static final short PATCH_BF = 25;
	public static final short PATCH_BR = 26;
	public static final short LOOP_START = 27;
	public static final short REPEAT = 28;
	public static final short COND_REPEAT = 29;
	public static final short EXIT = 30;
	public static final short COND_EXIT = 31;
	public static final short PATCH_EXIT_LIST = 32;
	public static final short ADDRESS = 33;
	public static final short INDEX_1D = 34;
	public static final short COND_ASSIGN = 35;
	public static final short ROUTINE_ENTRY = 36;
	public static final short ALLOC_FRAME = 37;
	public static final short PATCH_FRAME = 38;
	public static final short FREE_FRAME = 39;
	public static final short UPDATE_DISPLAY = 40;
	public static final short CALL_ROUTINE = 41;
	public static final short RETURN = 42;
	public static final short INIT_FUNC_FRAME = 43;
	public static final short INIT_PROC_FRAME = 44;
	public static final short COPY = 45;
	public static final short ROUTINE_EXIT = 46;
	public static final short ROUTINE_RETURN = 47;
	public static final short RESTORE_DISPLAY = 48;
	public static final short INDEX_2D = 49;

	public static class Operand
	{
		public static final short NONE = 0x0000;
		public static final short REGISTER = 0x0010;
		public static final short PATCH = 0x0020;
		public static final short PTR = 0x0040;
		public static final short LEXICAL_LEVEL = 0x000F;

		private short flags;
		private short value;

		public Operand(short flags, short value) // used for constant
		{
			this(flags, (short) 0, value);
		}

		public Operand(short flags, short ll, short value) // used for register
		{
			this.flags = (short) (flags | (ll & 0xF));
			this.value = value;
		}

		public boolean is_register() {
			return (this.flags & REGISTER) != 0;
		}

		public boolean is_pointer() {
			return (this.flags & PTR) != 0;
		}

		public boolean is_reg_or_ptr() {
			return (this.flags & (REGISTER | PTR)) != 0;
		}

		public boolean needs_patch() {
			return (this.flags & PATCH) != 0;
		}

		public short get_lexical_level() {
			return (short) (this.flags & LEXICAL_LEVEL);
		}

		public short get_value() {
			return this.value;
		}
	}

	public short opcode;
	public List<Operand> operands;

	public IR(short opcode)
	{
		this.opcode = opcode;
	}

	public IR(short opcode, Operand... operands)
	{
		this.opcode = opcode;
		this.operands = Arrays.asList(operands);
	}
}

package compiler488.codegen;

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
	public static final short AND = 12;
	public static final short OR = 13;
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
	public static final short INDEX = 34;
	public static final short COND_ASSIGN = 35;
	public static final short ALLOCATE = 36;
	public static final short PATCH_ALLOCATE = 37;
	public static final short MINOR_CLEANUP = 38;

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
	public Operand op1;
	public Operand op2;
	public Operand op3;
	public Operand op4;

	public IR(short opcode)
	{
		this.opcode = opcode;
	}

	public IR(short opcode, Operand op1)
	{
		this.opcode = opcode;
		this.op1 = op1;
	}

	public IR(short opcode, Operand op1, Operand op2)
	{
		this.opcode = opcode;
		this.op1 = op1;
		this.op2 = op2;
	}

	public IR(short opcode, Operand op1, Operand op2, Operand op3)
	{
		this.opcode = opcode;
		this.op1 = op1;
		this.op2 = op2;
		this.op3 = op3;
	}

	public IR(short opcode, Operand op1, Operand op2, Operand op3, Operand op4)
	{
		this.opcode = opcode;
		this.op1 = op1;
		this.op2 = op2;
		this.op3 = op3;
		this.op4 = op4;
	}
}

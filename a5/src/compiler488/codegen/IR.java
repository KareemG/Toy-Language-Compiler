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
	
	public static class Operand
	{
		public static final int NONE = 0x00000000;
		public static final int REGISTER = 0x00010000;
		public static final int PATCH = 0x00020000;

		private int value;

		public Operand(int flags, short value)
		{
			this.value = flags | ((int) value);
		}

		public boolean is_register() {
			return (this.value & REGISTER) != 0;
		}

		public boolean needs_patch() {
			return (this.value & PATCH) != 0;
		}

		public short get_value() {
			return (short) (this.value & 0x0000FFFF);
		}
	}

	public short opcode;
    public Operand op1;
    public Operand op2;
	public Operand op3;

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
}

package compiler488.ast.expn;

import compiler488.semantics.ASTVisitor;
import compiler488.ast.*;

/** Represents a conditional expression (i.e., x>0?3:4). */
public class ConditionalExpn extends Expn {
	private Expn condition; // Evaluate this to decide which value to yield.

	private Expn trueValue; // The value is this when the condition is true.

	private Expn falseValue; // Otherwise, the value is this.

	public ConditionalExpn(Expn condition, Expn trueValue, Expn falseValue) {
		super();

		this.condition = condition;
		this.trueValue = trueValue;
		this.falseValue = falseValue;
	}

	/** Returns a string that describes the conditional expression. */
	@Override
	public String toString() {
		return "(" + condition + " ? " + trueValue + " : " + falseValue + ")";
	}

	@Override
	public void prettyPrint(PrettyPrinter p)
	{
		p.print("(");
		condition.prettyPrint(p);
		p.print("?");
		trueValue.prettyPrint(p);
		p.print(":");
		falseValue.prettyPrint(p);
		p.print(")");
	}

	public Expn getCondition() {
		return condition;
	}

	public void setCondition(Expn condition) {
		this.condition = condition;
	}

	public Expn getFalseValue() {
		return falseValue;
	}

	public void setFalseValue(Expn falseValue) {
		this.falseValue = falseValue;
	}

	public Expn getTrueValue() {
		return trueValue;
	}

	public void setTrueValue(Expn trueValue) {
		this.trueValue = trueValue;
	}

	@Override
	public void accept(ASTVisitor visitor) {
		this.condition.accept(visitor);
		visitor.visitEnter(this);
		this.trueValue.accept(visitor);
		visitor.visit(this);
		this.falseValue.accept(visitor);
		visitor.visitLeave(this);
	}
}

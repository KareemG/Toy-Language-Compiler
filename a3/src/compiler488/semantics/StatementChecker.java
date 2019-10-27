package compiler488.semantics;

import compiler488.ast.decl.*;
import compiler488.ast.stmt.*;

public class StatementChecker extends BaseVisitor
{
    private class Entry
    {
        public final static int GLOBAL = 0;
        public final static int FUNCTION = 1;
        public final static int PROCEDURE = 2;

        public int type;
        public int loop_count;
        public int return_count;

        public Entry prev;

        public Entry(int t)
        {
            type = t;
            loop_count = 0;
            return_count = 0;

            prev = null;
        }
    }

    private Entry current;

    public StatementChecker()
    {
        current = new Entry(Entry.GLOBAL);
    }

    public void visitEnter(RoutineDecl node)
    {
        Entry entry = new Entry(node.getType() != null ? Entry.FUNCTION : Entry.PROCEDURE);
        
        entry.prev = current;
        current = entry;
    }
    
    public void visitLeave(RoutineDecl node)
    {
        if((current.type == Entry.FUNCTION) && (current.return_count == 0))
        {
            System.out.println("S54: function must have at least one return statement");
        }

        current = current.prev;
    }

    public void visit(ReturnStmt node)
    {
        if((current.type != Entry.FUNCTION) && (node.getValue() != null))
        {
            System.out.println("S52: return with statement must be in function");
        }
        else if((current.type != Entry.PROCEDURE) && (node.getValue() == null))
        {
            System.out.println("S51: return statement must be in procedure");
        }
        
        current.return_count ++;
    }

    public void visitEnter(LoopingStmt node)
    {
        current.loop_count ++;
    }

    public void visitLeave(LoopingStmt node)
    {
        current.loop_count --;
    }

    public void visit(ExitStmt node)
    {
        if(current == null || current.loop_count == 0)
        {
            System.out.println("S50: exit statements must be inside a loop");
        }
        else if(node.getLevel() == 0)
        {
            System.out.println("S53: exit integer must be greater than 0");
        }
        else if(node.getLevel() > current.loop_count)
        {
            System.out.println("S53: exit integer must be less than number of loops");
        }
    }
}

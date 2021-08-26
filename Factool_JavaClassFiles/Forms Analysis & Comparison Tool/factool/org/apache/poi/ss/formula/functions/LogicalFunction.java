// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.eval.AreaEval;
import org.apache.poi.ss.formula.eval.RefEval;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.BlankEval;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.StringEval;
import org.apache.poi.ss.formula.eval.BoolEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.ValueEval;

public abstract class LogicalFunction extends Fixed1ArgFunction
{
    public static final Function ISLOGICAL;
    public static final Function ISNONTEXT;
    public static final Function ISNUMBER;
    public static final Function ISTEXT;
    public static final Function ISBLANK;
    public static final Function ISERROR;
    public static final Function ISERR;
    public static final Function ISNA;
    public static final Function ISREF;
    
    @Override
    public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval arg0) {
        ValueEval ve;
        try {
            ve = OperandResolver.getSingleValue(arg0, srcRowIndex, srcColumnIndex);
        }
        catch (EvaluationException e) {
            ve = e.getErrorEval();
        }
        return BoolEval.valueOf(this.evaluate(ve));
    }
    
    protected abstract boolean evaluate(final ValueEval p0);
    
    static {
        ISLOGICAL = new LogicalFunction() {
            @Override
            protected boolean evaluate(final ValueEval arg) {
                return arg instanceof BoolEval;
            }
        };
        ISNONTEXT = new LogicalFunction() {
            @Override
            protected boolean evaluate(final ValueEval arg) {
                return !(arg instanceof StringEval);
            }
        };
        ISNUMBER = new LogicalFunction() {
            @Override
            protected boolean evaluate(final ValueEval arg) {
                return arg instanceof NumberEval;
            }
        };
        ISTEXT = new LogicalFunction() {
            @Override
            protected boolean evaluate(final ValueEval arg) {
                return arg instanceof StringEval;
            }
        };
        ISBLANK = new LogicalFunction() {
            @Override
            protected boolean evaluate(final ValueEval arg) {
                return arg instanceof BlankEval;
            }
        };
        ISERROR = new LogicalFunction() {
            @Override
            protected boolean evaluate(final ValueEval arg) {
                return arg instanceof ErrorEval;
            }
        };
        ISERR = new LogicalFunction() {
            @Override
            protected boolean evaluate(final ValueEval arg) {
                return arg instanceof ErrorEval && arg != ErrorEval.NA;
            }
        };
        ISNA = new LogicalFunction() {
            @Override
            protected boolean evaluate(final ValueEval arg) {
                return arg == ErrorEval.NA;
            }
        };
        ISREF = new Fixed1ArgFunction() {
            @Override
            public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval arg0) {
                if (arg0 instanceof RefEval || arg0 instanceof AreaEval) {
                    return BoolEval.TRUE;
                }
                return BoolEval.FALSE;
            }
        };
    }
}

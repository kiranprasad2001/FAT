// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.eval;

import org.apache.poi.ss.util.NumberComparer;
import org.apache.poi.ss.formula.functions.Function;
import org.apache.poi.ss.formula.functions.Fixed2ArgFunction;

public abstract class RelationalOperationEval extends Fixed2ArgFunction
{
    public static final Function EqualEval;
    public static final Function GreaterEqualEval;
    public static final Function GreaterThanEval;
    public static final Function LessEqualEval;
    public static final Function LessThanEval;
    public static final Function NotEqualEval;
    
    protected abstract boolean convertComparisonResult(final int p0);
    
    @Override
    public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval arg0, final ValueEval arg1) {
        ValueEval vA;
        ValueEval vB;
        try {
            vA = OperandResolver.getSingleValue(arg0, srcRowIndex, srcColumnIndex);
            vB = OperandResolver.getSingleValue(arg1, srcRowIndex, srcColumnIndex);
        }
        catch (EvaluationException e) {
            return e.getErrorEval();
        }
        final int cmpResult = doCompare(vA, vB);
        final boolean result = this.convertComparisonResult(cmpResult);
        return BoolEval.valueOf(result);
    }
    
    private static int doCompare(final ValueEval va, final ValueEval vb) {
        if (va == BlankEval.instance) {
            return compareBlank(vb);
        }
        if (vb == BlankEval.instance) {
            return -compareBlank(va);
        }
        if (va instanceof BoolEval) {
            if (!(vb instanceof BoolEval)) {
                return 1;
            }
            final BoolEval bA = (BoolEval)va;
            final BoolEval bB = (BoolEval)vb;
            if (bA.getBooleanValue() == bB.getBooleanValue()) {
                return 0;
            }
            return bA.getBooleanValue() ? 1 : -1;
        }
        else {
            if (vb instanceof BoolEval) {
                return -1;
            }
            if (va instanceof StringEval) {
                if (vb instanceof StringEval) {
                    final StringEval sA = (StringEval)va;
                    final StringEval sB = (StringEval)vb;
                    return sA.getStringValue().compareToIgnoreCase(sB.getStringValue());
                }
                return 1;
            }
            else {
                if (vb instanceof StringEval) {
                    return -1;
                }
                if (va instanceof NumberEval && vb instanceof NumberEval) {
                    final NumberEval nA = (NumberEval)va;
                    final NumberEval nB = (NumberEval)vb;
                    return NumberComparer.compare(nA.getNumberValue(), nB.getNumberValue());
                }
                throw new IllegalArgumentException("Bad operand types (" + va.getClass().getName() + "), (" + vb.getClass().getName() + ")");
            }
        }
    }
    
    private static int compareBlank(final ValueEval v) {
        if (v == BlankEval.instance) {
            return 0;
        }
        if (v instanceof BoolEval) {
            final BoolEval boolEval = (BoolEval)v;
            return boolEval.getBooleanValue() ? -1 : 0;
        }
        if (v instanceof NumberEval) {
            final NumberEval ne = (NumberEval)v;
            return NumberComparer.compare(0.0, ne.getNumberValue());
        }
        if (v instanceof StringEval) {
            final StringEval se = (StringEval)v;
            return (se.getStringValue().length() < 1) ? 0 : -1;
        }
        throw new IllegalArgumentException("bad value class (" + v.getClass().getName() + ")");
    }
    
    static {
        EqualEval = new RelationalOperationEval() {
            @Override
            protected boolean convertComparisonResult(final int cmpResult) {
                return cmpResult == 0;
            }
        };
        GreaterEqualEval = new RelationalOperationEval() {
            @Override
            protected boolean convertComparisonResult(final int cmpResult) {
                return cmpResult >= 0;
            }
        };
        GreaterThanEval = new RelationalOperationEval() {
            @Override
            protected boolean convertComparisonResult(final int cmpResult) {
                return cmpResult > 0;
            }
        };
        LessEqualEval = new RelationalOperationEval() {
            @Override
            protected boolean convertComparisonResult(final int cmpResult) {
                return cmpResult <= 0;
            }
        };
        LessThanEval = new RelationalOperationEval() {
            @Override
            protected boolean convertComparisonResult(final int cmpResult) {
                return cmpResult < 0;
            }
        };
        NotEqualEval = new RelationalOperationEval() {
            @Override
            protected boolean convertComparisonResult(final int cmpResult) {
                return cmpResult != 0;
            }
        };
    }
}

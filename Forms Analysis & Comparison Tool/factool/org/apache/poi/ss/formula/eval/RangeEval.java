// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.eval;

import org.apache.poi.ss.formula.functions.Function;
import org.apache.poi.ss.formula.functions.Fixed2ArgFunction;

public final class RangeEval extends Fixed2ArgFunction
{
    public static final Function instance;
    
    private RangeEval() {
    }
    
    @Override
    public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval arg0, final ValueEval arg1) {
        try {
            final AreaEval reA = evaluateRef(arg0);
            final AreaEval reB = evaluateRef(arg1);
            return resolveRange(reA, reB);
        }
        catch (EvaluationException e) {
            return e.getErrorEval();
        }
    }
    
    private static AreaEval resolveRange(final AreaEval aeA, final AreaEval aeB) {
        final int aeAfr = aeA.getFirstRow();
        final int aeAfc = aeA.getFirstColumn();
        final int top = Math.min(aeAfr, aeB.getFirstRow());
        final int bottom = Math.max(aeA.getLastRow(), aeB.getLastRow());
        final int left = Math.min(aeAfc, aeB.getFirstColumn());
        final int right = Math.max(aeA.getLastColumn(), aeB.getLastColumn());
        return aeA.offset(top - aeAfr, bottom - aeAfr, left - aeAfc, right - aeAfc);
    }
    
    private static AreaEval evaluateRef(final ValueEval arg) throws EvaluationException {
        if (arg instanceof AreaEval) {
            return (AreaEval)arg;
        }
        if (arg instanceof RefEval) {
            return ((RefEval)arg).offset(0, 0, 0, 0);
        }
        if (arg instanceof ErrorEval) {
            throw new EvaluationException((ErrorEval)arg);
        }
        throw new IllegalArgumentException("Unexpected ref arg class (" + arg.getClass().getName() + ")");
    }
    
    static {
        instance = new RangeEval();
    }
}

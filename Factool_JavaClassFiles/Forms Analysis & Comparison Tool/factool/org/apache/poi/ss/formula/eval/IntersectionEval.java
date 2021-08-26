// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.eval;

import org.apache.poi.ss.formula.functions.Function;
import org.apache.poi.ss.formula.functions.Fixed2ArgFunction;

public final class IntersectionEval extends Fixed2ArgFunction
{
    public static final Function instance;
    
    private IntersectionEval() {
    }
    
    @Override
    public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval arg0, final ValueEval arg1) {
        try {
            final AreaEval reA = evaluateRef(arg0);
            final AreaEval reB = evaluateRef(arg1);
            final AreaEval result = resolveRange(reA, reB);
            if (result == null) {
                return ErrorEval.NULL_INTERSECTION;
            }
            return result;
        }
        catch (EvaluationException e) {
            return e.getErrorEval();
        }
    }
    
    private static AreaEval resolveRange(final AreaEval aeA, final AreaEval aeB) {
        final int aeAfr = aeA.getFirstRow();
        final int aeAfc = aeA.getFirstColumn();
        final int aeBlc = aeB.getLastColumn();
        if (aeAfc > aeBlc) {
            return null;
        }
        final int aeBfc = aeB.getFirstColumn();
        if (aeBfc > aeA.getLastColumn()) {
            return null;
        }
        final int aeBlr = aeB.getLastRow();
        if (aeAfr > aeBlr) {
            return null;
        }
        final int aeBfr = aeB.getFirstRow();
        final int aeAlr = aeA.getLastRow();
        if (aeBfr > aeAlr) {
            return null;
        }
        final int top = Math.max(aeAfr, aeBfr);
        final int bottom = Math.min(aeAlr, aeBlr);
        final int left = Math.max(aeAfc, aeBfc);
        final int right = Math.min(aeA.getLastColumn(), aeBlc);
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
        instance = new IntersectionEval();
    }
}

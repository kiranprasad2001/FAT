// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.RefEval;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.AreaEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.ValueEval;

public final class Sumif extends Var2or3ArgFunction
{
    @Override
    public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval arg0, final ValueEval arg1) {
        AreaEval aeRange;
        try {
            aeRange = convertRangeArg(arg0);
        }
        catch (EvaluationException e) {
            return e.getErrorEval();
        }
        return eval(srcRowIndex, srcColumnIndex, arg1, aeRange, aeRange);
    }
    
    @Override
    public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval arg0, final ValueEval arg1, final ValueEval arg2) {
        AreaEval aeRange;
        AreaEval aeSum;
        try {
            aeRange = convertRangeArg(arg0);
            aeSum = createSumRange(arg2, aeRange);
        }
        catch (EvaluationException e) {
            return e.getErrorEval();
        }
        return eval(srcRowIndex, srcColumnIndex, arg1, aeRange, aeSum);
    }
    
    private static ValueEval eval(final int srcRowIndex, final int srcColumnIndex, final ValueEval arg1, final AreaEval aeRange, final AreaEval aeSum) {
        final CountUtils.I_MatchPredicate mp = Countif.createCriteriaPredicate(arg1, srcRowIndex, srcColumnIndex);
        final double result = sumMatchingCells(aeRange, mp, aeSum);
        return new NumberEval(result);
    }
    
    private static double sumMatchingCells(final AreaEval aeRange, final CountUtils.I_MatchPredicate mp, final AreaEval aeSum) {
        final int height = aeRange.getHeight();
        final int width = aeRange.getWidth();
        double result = 0.0;
        for (int r = 0; r < height; ++r) {
            for (int c = 0; c < width; ++c) {
                result += accumulate(aeRange, mp, aeSum, r, c);
            }
        }
        return result;
    }
    
    private static double accumulate(final AreaEval aeRange, final CountUtils.I_MatchPredicate mp, final AreaEval aeSum, final int relRowIndex, final int relColIndex) {
        if (!mp.matches(aeRange.getRelativeValue(relRowIndex, relColIndex))) {
            return 0.0;
        }
        final ValueEval addend = aeSum.getRelativeValue(relRowIndex, relColIndex);
        if (addend instanceof NumberEval) {
            return ((NumberEval)addend).getNumberValue();
        }
        return 0.0;
    }
    
    private static AreaEval createSumRange(final ValueEval eval, final AreaEval aeRange) throws EvaluationException {
        if (eval instanceof AreaEval) {
            return ((AreaEval)eval).offset(0, aeRange.getHeight() - 1, 0, aeRange.getWidth() - 1);
        }
        if (eval instanceof RefEval) {
            return ((RefEval)eval).offset(0, aeRange.getHeight() - 1, 0, aeRange.getWidth() - 1);
        }
        throw new EvaluationException(ErrorEval.VALUE_INVALID);
    }
    
    private static AreaEval convertRangeArg(final ValueEval eval) throws EvaluationException {
        if (eval instanceof AreaEval) {
            return (AreaEval)eval;
        }
        if (eval instanceof RefEval) {
            return ((RefEval)eval).offset(0, 0, 0, 0);
        }
        throw new EvaluationException(ErrorEval.VALUE_INVALID);
    }
}

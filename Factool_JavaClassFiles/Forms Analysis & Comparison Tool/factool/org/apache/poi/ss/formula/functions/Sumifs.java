// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.eval.RefEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.AreaEval;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.OperationEvaluationContext;
import org.apache.poi.ss.formula.eval.ValueEval;

public final class Sumifs implements FreeRefFunction
{
    public static final FreeRefFunction instance;
    
    @Override
    public ValueEval evaluate(final ValueEval[] args, final OperationEvaluationContext ec) {
        if (args.length < 3 || args.length % 2 == 0) {
            return ErrorEval.VALUE_INVALID;
        }
        try {
            final AreaEval sumRange = convertRangeArg(args[0]);
            final AreaEval[] ae = new AreaEval[(args.length - 1) / 2];
            final CountUtils.I_MatchPredicate[] mp = new CountUtils.I_MatchPredicate[ae.length];
            for (int i = 1, k = 0; i < args.length; i += 2, ++k) {
                ae[k] = convertRangeArg(args[i]);
                mp[k] = Countif.createCriteriaPredicate(args[i + 1], ec.getRowIndex(), ec.getColumnIndex());
            }
            this.validateCriteriaRanges(ae, sumRange);
            final double result = sumMatchingCells(ae, mp, sumRange);
            return new NumberEval(result);
        }
        catch (EvaluationException e) {
            return e.getErrorEval();
        }
    }
    
    private void validateCriteriaRanges(final AreaEval[] criteriaRanges, final AreaEval sumRange) throws EvaluationException {
        for (final AreaEval r : criteriaRanges) {
            if (r.getHeight() != sumRange.getHeight() || r.getWidth() != sumRange.getWidth()) {
                throw EvaluationException.invalidValue();
            }
        }
    }
    
    private static double sumMatchingCells(final AreaEval[] ranges, final CountUtils.I_MatchPredicate[] predicates, final AreaEval aeSum) {
        final int height = aeSum.getHeight();
        final int width = aeSum.getWidth();
        double result = 0.0;
        for (int r = 0; r < height; ++r) {
            for (int c = 0; c < width; ++c) {
                boolean matches = true;
                for (int i = 0; i < ranges.length; ++i) {
                    final AreaEval aeRange = ranges[i];
                    final CountUtils.I_MatchPredicate mp = predicates[i];
                    if (!mp.matches(aeRange.getRelativeValue(r, c))) {
                        matches = false;
                        break;
                    }
                }
                if (matches) {
                    result += accumulate(aeSum, r, c);
                }
            }
        }
        return result;
    }
    
    private static double accumulate(final AreaEval aeSum, final int relRowIndex, final int relColIndex) {
        final ValueEval addend = aeSum.getRelativeValue(relRowIndex, relColIndex);
        if (addend instanceof NumberEval) {
            return ((NumberEval)addend).getNumberValue();
        }
        return 0.0;
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
    
    static {
        instance = new Sumifs();
    }
}

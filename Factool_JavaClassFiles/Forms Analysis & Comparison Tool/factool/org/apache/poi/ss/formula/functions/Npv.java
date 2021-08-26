// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.ValueEval;

public final class Npv implements Function
{
    @Override
    public ValueEval evaluate(final ValueEval[] args, final int srcRowIndex, final int srcColumnIndex) {
        final int nArgs = args.length;
        if (nArgs < 2) {
            return ErrorEval.VALUE_INVALID;
        }
        try {
            final double rate = NumericFunction.singleOperandEvaluate(args[0], srcRowIndex, srcColumnIndex);
            final ValueEval[] vargs = new ValueEval[args.length - 1];
            System.arraycopy(args, 1, vargs, 0, vargs.length);
            final double[] values = AggregateFunction.ValueCollector.collectValues(vargs);
            final double result = FinanceLib.npv(rate, values);
            NumericFunction.checkValue(result);
            return new NumberEval(result);
        }
        catch (EvaluationException e) {
            return e.getErrorEval();
        }
    }
}

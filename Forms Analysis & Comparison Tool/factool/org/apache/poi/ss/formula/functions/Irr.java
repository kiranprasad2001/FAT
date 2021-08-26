// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.ValueEval;

public final class Irr implements Function
{
    @Override
    public ValueEval evaluate(final ValueEval[] args, final int srcRowIndex, final int srcColumnIndex) {
        if (args.length == 0 || args.length > 2) {
            return ErrorEval.VALUE_INVALID;
        }
        try {
            final double[] values = AggregateFunction.ValueCollector.collectValues(args[0]);
            double guess;
            if (args.length == 2) {
                guess = NumericFunction.singleOperandEvaluate(args[1], srcRowIndex, srcColumnIndex);
            }
            else {
                guess = 0.1;
            }
            final double result = irr(values, guess);
            NumericFunction.checkValue(result);
            return new NumberEval(result);
        }
        catch (EvaluationException e) {
            return e.getErrorEval();
        }
    }
    
    public static double irr(final double[] income) {
        return irr(income, 0.1);
    }
    
    public static double irr(final double[] values, final double guess) {
        final int maxIterationCount = 20;
        final double absoluteAccuracy = 1.0E-7;
        double x0 = guess;
        for (int i = 0; i < maxIterationCount; ++i) {
            double fValue = 0.0;
            double fDerivative = 0.0;
            for (int k = 0; k < values.length; ++k) {
                fValue += values[k] / Math.pow(1.0 + x0, k);
                fDerivative += -k * values[k] / Math.pow(1.0 + x0, k + 1);
            }
            final double x2 = x0 - fValue / fDerivative;
            if (Math.abs(x2 - x0) <= absoluteAccuracy) {
                return x2;
            }
            x0 = x2;
        }
        return Double.NaN;
    }
}

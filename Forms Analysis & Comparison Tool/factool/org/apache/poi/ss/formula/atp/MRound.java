// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.atp;

import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.functions.NumericFunction;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.OperationEvaluationContext;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.functions.FreeRefFunction;

final class MRound implements FreeRefFunction
{
    public static final FreeRefFunction instance;
    
    private MRound() {
    }
    
    @Override
    public ValueEval evaluate(final ValueEval[] args, final OperationEvaluationContext ec) {
        if (args.length != 2) {
            return ErrorEval.VALUE_INVALID;
        }
        try {
            final double number = OperandResolver.coerceValueToDouble(OperandResolver.getSingleValue(args[0], ec.getRowIndex(), ec.getColumnIndex()));
            final double multiple = OperandResolver.coerceValueToDouble(OperandResolver.getSingleValue(args[1], ec.getRowIndex(), ec.getColumnIndex()));
            double result;
            if (multiple == 0.0) {
                result = 0.0;
            }
            else {
                if (number * multiple < 0.0) {
                    throw new EvaluationException(ErrorEval.NUM_ERROR);
                }
                result = multiple * Math.round(number / multiple);
            }
            NumericFunction.checkValue(result);
            return new NumberEval(result);
        }
        catch (EvaluationException e) {
            return e.getErrorEval();
        }
    }
    
    static {
        instance = new MRound();
    }
}

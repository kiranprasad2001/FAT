// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.OperationEvaluationContext;
import org.apache.poi.ss.formula.eval.ValueEval;

public class Countifs implements FreeRefFunction
{
    public static final FreeRefFunction instance;
    
    @Override
    public ValueEval evaluate(final ValueEval[] args, final OperationEvaluationContext ec) {
        Double result = null;
        if (args.length == 0 || args.length % 2 > 0) {
            return ErrorEval.VALUE_INVALID;
        }
        int i = 0;
        while (i < args.length) {
            final ValueEval firstArg = args[i];
            final ValueEval secondArg = args[i + 1];
            i += 2;
            final NumberEval evaluate = (NumberEval)new Countif().evaluate(new ValueEval[] { firstArg, secondArg }, ec.getRowIndex(), ec.getColumnIndex());
            if (result == null) {
                result = evaluate.getNumberValue();
            }
            else {
                if (evaluate.getNumberValue() >= result) {
                    continue;
                }
                result = evaluate.getNumberValue();
            }
        }
        return new NumberEval((result == null) ? 0.0 : result);
    }
    
    static {
        instance = new Countifs();
    }
}

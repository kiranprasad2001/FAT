// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.BlankEval;
import org.apache.poi.ss.formula.eval.MissingArgEval;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.ValueEval;

public final class Choose implements Function
{
    @Override
    public ValueEval evaluate(final ValueEval[] args, final int srcRowIndex, final int srcColumnIndex) {
        if (args.length < 2) {
            return ErrorEval.VALUE_INVALID;
        }
        try {
            final int ix = evaluateFirstArg(args[0], srcRowIndex, srcColumnIndex);
            if (ix < 1 || ix >= args.length) {
                return ErrorEval.VALUE_INVALID;
            }
            final ValueEval result = OperandResolver.getSingleValue(args[ix], srcRowIndex, srcColumnIndex);
            if (result == MissingArgEval.instance) {
                return BlankEval.instance;
            }
            return result;
        }
        catch (EvaluationException e) {
            return e.getErrorEval();
        }
    }
    
    public static int evaluateFirstArg(final ValueEval arg0, final int srcRowIndex, final int srcColumnIndex) throws EvaluationException {
        final ValueEval ev = OperandResolver.getSingleValue(arg0, srcRowIndex, srcColumnIndex);
        return OperandResolver.coerceValueToInt(ev);
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.MissingArgEval;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.ValueEval;

public final class TimeFunc extends Fixed3ArgFunction
{
    private static final int SECONDS_PER_MINUTE = 60;
    private static final int SECONDS_PER_HOUR = 3600;
    private static final int HOURS_PER_DAY = 24;
    private static final int SECONDS_PER_DAY = 86400;
    
    @Override
    public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval arg0, final ValueEval arg1, final ValueEval arg2) {
        double result;
        try {
            result = evaluate(evalArg(arg0, srcRowIndex, srcColumnIndex), evalArg(arg1, srcRowIndex, srcColumnIndex), evalArg(arg2, srcRowIndex, srcColumnIndex));
        }
        catch (EvaluationException e) {
            return e.getErrorEval();
        }
        return new NumberEval(result);
    }
    
    private static int evalArg(final ValueEval arg, final int srcRowIndex, final int srcColumnIndex) throws EvaluationException {
        if (arg == MissingArgEval.instance) {
            return 0;
        }
        final ValueEval ev = OperandResolver.getSingleValue(arg, srcRowIndex, srcColumnIndex);
        return OperandResolver.coerceValueToInt(ev);
    }
    
    private static double evaluate(final int hours, final int minutes, final int seconds) throws EvaluationException {
        if (hours > 32767 || minutes > 32767 || seconds > 32767) {
            throw new EvaluationException(ErrorEval.VALUE_INVALID);
        }
        final int totalSeconds = hours * 3600 + minutes * 60 + seconds;
        if (totalSeconds < 0) {
            throw new EvaluationException(ErrorEval.VALUE_INVALID);
        }
        return totalSeconds % 86400 / 86400.0;
    }
}

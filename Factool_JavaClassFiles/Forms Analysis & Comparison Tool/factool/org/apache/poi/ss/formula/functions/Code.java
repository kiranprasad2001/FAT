// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.eval.StringEval;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.ValueEval;

public class Code extends Fixed1ArgFunction
{
    @Override
    public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval textArg) {
        ValueEval veText1;
        try {
            veText1 = OperandResolver.getSingleValue(textArg, srcRowIndex, srcColumnIndex);
        }
        catch (EvaluationException e) {
            return e.getErrorEval();
        }
        final String text = OperandResolver.coerceValueToString(veText1);
        if (text.length() == 0) {
            return ErrorEval.VALUE_INVALID;
        }
        final int code = text.charAt(0);
        return new StringEval(String.valueOf(code));
    }
}

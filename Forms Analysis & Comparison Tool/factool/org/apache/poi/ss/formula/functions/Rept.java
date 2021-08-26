// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.eval.StringEval;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.ValueEval;

public class Rept extends Fixed2ArgFunction
{
    @Override
    public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval text, final ValueEval number_times) {
        ValueEval veText1;
        try {
            veText1 = OperandResolver.getSingleValue(text, srcRowIndex, srcColumnIndex);
        }
        catch (EvaluationException e) {
            return e.getErrorEval();
        }
        final String strText1 = OperandResolver.coerceValueToString(veText1);
        double numberOfTime = 0.0;
        try {
            numberOfTime = OperandResolver.coerceValueToDouble(number_times);
        }
        catch (EvaluationException e2) {
            return ErrorEval.VALUE_INVALID;
        }
        final int numberOfTimeInt = new Double(numberOfTime).intValue();
        final StringBuffer strb = new StringBuffer(strText1.length() * numberOfTimeInt);
        for (int i = 0; i < numberOfTimeInt; ++i) {
            strb.append(strText1);
        }
        if (strb.toString().length() > 32767) {
            return ErrorEval.VALUE_INVALID;
        }
        return new StringEval(strb.toString());
    }
}

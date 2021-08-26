// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.OperationEvaluationContext;
import java.math.BigDecimal;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.eval.NumberEval;

public final class Delta extends Fixed2ArgFunction implements FreeRefFunction
{
    public static final FreeRefFunction instance;
    private static final NumberEval ONE;
    private static final NumberEval ZERO;
    
    @Override
    public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval arg1, final ValueEval arg2) {
        ValueEval veText1;
        try {
            veText1 = OperandResolver.getSingleValue(arg1, srcRowIndex, srcColumnIndex);
        }
        catch (EvaluationException e) {
            return e.getErrorEval();
        }
        final String strText1 = OperandResolver.coerceValueToString(veText1);
        final Double number1 = OperandResolver.parseDouble(strText1);
        if (number1 == null) {
            return ErrorEval.VALUE_INVALID;
        }
        ValueEval veText2;
        try {
            veText2 = OperandResolver.getSingleValue(arg2, srcRowIndex, srcColumnIndex);
        }
        catch (EvaluationException e2) {
            return e2.getErrorEval();
        }
        final String strText2 = OperandResolver.coerceValueToString(veText2);
        final Double number2 = OperandResolver.parseDouble(strText2);
        if (number2 == null) {
            return ErrorEval.VALUE_INVALID;
        }
        final int result = new BigDecimal(number1).compareTo(new BigDecimal(number2));
        return (result == 0) ? Delta.ONE : Delta.ZERO;
    }
    
    @Override
    public ValueEval evaluate(final ValueEval[] args, final OperationEvaluationContext ec) {
        if (args.length == 2) {
            return this.evaluate(ec.getRowIndex(), ec.getColumnIndex(), args[0], args[1]);
        }
        return ErrorEval.VALUE_INVALID;
    }
    
    static {
        instance = new Delta();
        ONE = new NumberEval(1.0);
        ZERO = new NumberEval(0.0);
    }
}

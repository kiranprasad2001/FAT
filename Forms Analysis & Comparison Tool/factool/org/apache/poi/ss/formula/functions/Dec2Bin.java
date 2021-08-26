// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.OperationEvaluationContext;
import org.apache.poi.ss.formula.eval.StringEval;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.ValueEval;

public class Dec2Bin extends Var1or2ArgFunction implements FreeRefFunction
{
    public static final FreeRefFunction instance;
    private static final long MIN_VALUE = -512L;
    private static final long MAX_VALUE = 511L;
    private static final int DEFAULT_PLACES_VALUE = 10;
    
    @Override
    public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval numberVE, final ValueEval placesVE) {
        ValueEval veText1;
        try {
            veText1 = OperandResolver.getSingleValue(numberVE, srcRowIndex, srcColumnIndex);
        }
        catch (EvaluationException e) {
            return e.getErrorEval();
        }
        final String strText1 = OperandResolver.coerceValueToString(veText1);
        final Double number = OperandResolver.parseDouble(strText1);
        if (number == null) {
            return ErrorEval.VALUE_INVALID;
        }
        if (number.longValue() < -512L || number.longValue() > 511L) {
            return ErrorEval.NUM_ERROR;
        }
        int placesNumber;
        if (number < 0.0 || placesVE == null) {
            placesNumber = 10;
        }
        else {
            ValueEval placesValueEval;
            try {
                placesValueEval = OperandResolver.getSingleValue(placesVE, srcRowIndex, srcColumnIndex);
            }
            catch (EvaluationException e2) {
                return e2.getErrorEval();
            }
            final String placesStr = OperandResolver.coerceValueToString(placesValueEval);
            final Double placesNumberDouble = OperandResolver.parseDouble(placesStr);
            if (placesNumberDouble == null) {
                return ErrorEval.VALUE_INVALID;
            }
            placesNumber = placesNumberDouble.intValue();
            if (placesNumber < 0 || placesNumber == 0) {
                return ErrorEval.NUM_ERROR;
            }
        }
        String binary = Integer.toBinaryString(number.intValue());
        if (binary.length() > 10) {
            binary = binary.substring(binary.length() - 10, binary.length());
        }
        if (binary.length() > placesNumber) {
            return ErrorEval.NUM_ERROR;
        }
        return new StringEval(binary);
    }
    
    @Override
    public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval numberVE) {
        return this.evaluate(srcRowIndex, srcColumnIndex, numberVE, null);
    }
    
    @Override
    public ValueEval evaluate(final ValueEval[] args, final OperationEvaluationContext ec) {
        if (args.length == 1) {
            return this.evaluate(ec.getRowIndex(), ec.getColumnIndex(), args[0]);
        }
        if (args.length == 2) {
            return this.evaluate(ec.getRowIndex(), ec.getColumnIndex(), args[0], args[1]);
        }
        return ErrorEval.VALUE_INVALID;
    }
    
    static {
        instance = new Dec2Bin();
    }
}

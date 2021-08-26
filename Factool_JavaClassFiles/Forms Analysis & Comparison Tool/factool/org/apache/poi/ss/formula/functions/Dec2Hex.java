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

public final class Dec2Hex extends Var1or2ArgFunction implements FreeRefFunction
{
    public static final FreeRefFunction instance;
    private static final long MIN_VALUE;
    private static final long MAX_VALUE;
    private static final int DEFAULT_PLACES_VALUE = 10;
    
    @Override
    public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval number, final ValueEval places) {
        ValueEval veText1;
        try {
            veText1 = OperandResolver.getSingleValue(number, srcRowIndex, srcColumnIndex);
        }
        catch (EvaluationException e) {
            return e.getErrorEval();
        }
        final String strText1 = OperandResolver.coerceValueToString(veText1);
        final Double number2 = OperandResolver.parseDouble(strText1);
        if (number2 == null) {
            return ErrorEval.VALUE_INVALID;
        }
        if (number2.longValue() < Dec2Hex.MIN_VALUE || number2.longValue() > Dec2Hex.MAX_VALUE) {
            return ErrorEval.NUM_ERROR;
        }
        int placesNumber = 0;
        if (number2 < 0.0) {
            placesNumber = 10;
        }
        else if (places != null) {
            ValueEval placesValueEval;
            try {
                placesValueEval = OperandResolver.getSingleValue(places, srcRowIndex, srcColumnIndex);
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
            if (placesNumber < 0) {
                return ErrorEval.NUM_ERROR;
            }
        }
        String hex;
        if (placesNumber != 0) {
            hex = String.format("%0" + placesNumber + "X", number2.intValue());
        }
        else {
            hex = Integer.toHexString(number2.intValue());
        }
        if (number2 < 0.0) {
            hex = "FF" + hex.substring(2);
        }
        return new StringEval(hex.toUpperCase());
    }
    
    @Override
    public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval arg0) {
        return this.evaluate(srcRowIndex, srcColumnIndex, arg0, null);
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
        instance = new Dec2Hex();
        MIN_VALUE = Long.parseLong("-549755813888");
        MAX_VALUE = Long.parseLong("549755813887");
    }
}

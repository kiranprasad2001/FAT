// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.NotImplementedException;
import org.apache.poi.ss.formula.eval.NotImplementedFunctionException;

public class Subtotal implements Function
{
    private static Function findFunction(final int functionCode) throws EvaluationException {
        switch (functionCode) {
            case 1: {
                return AggregateFunction.subtotalInstance(AggregateFunction.AVERAGE);
            }
            case 2: {
                return Count.subtotalInstance();
            }
            case 3: {
                return Counta.subtotalInstance();
            }
            case 4: {
                return AggregateFunction.subtotalInstance(AggregateFunction.MAX);
            }
            case 5: {
                return AggregateFunction.subtotalInstance(AggregateFunction.MIN);
            }
            case 6: {
                return AggregateFunction.subtotalInstance(AggregateFunction.PRODUCT);
            }
            case 7: {
                return AggregateFunction.subtotalInstance(AggregateFunction.STDEV);
            }
            case 8: {
                throw new NotImplementedFunctionException("STDEVP");
            }
            case 9: {
                return AggregateFunction.subtotalInstance(AggregateFunction.SUM);
            }
            case 10: {
                throw new NotImplementedFunctionException("VAR");
            }
            case 11: {
                throw new NotImplementedFunctionException("VARP");
            }
            default: {
                if (functionCode > 100 && functionCode < 112) {
                    throw new NotImplementedException("SUBTOTAL - with 'exclude hidden values' option");
                }
                throw EvaluationException.invalidValue();
            }
        }
    }
    
    @Override
    public ValueEval evaluate(final ValueEval[] args, final int srcRowIndex, final int srcColumnIndex) {
        final int nInnerArgs = args.length - 1;
        if (nInnerArgs < 1) {
            return ErrorEval.VALUE_INVALID;
        }
        Function innerFunc;
        try {
            final ValueEval ve = OperandResolver.getSingleValue(args[0], srcRowIndex, srcColumnIndex);
            final int functionCode = OperandResolver.coerceValueToInt(ve);
            innerFunc = findFunction(functionCode);
        }
        catch (EvaluationException e) {
            return e.getErrorEval();
        }
        final ValueEval[] innerArgs = new ValueEval[nInnerArgs];
        System.arraycopy(args, 1, innerArgs, 0, nInnerArgs);
        return innerFunc.evaluate(innerArgs, srcRowIndex, srcColumnIndex);
    }
}

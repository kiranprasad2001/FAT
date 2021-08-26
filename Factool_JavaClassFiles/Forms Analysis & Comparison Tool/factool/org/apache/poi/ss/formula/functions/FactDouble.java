// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.OperationEvaluationContext;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.ValueEval;
import java.math.BigInteger;
import java.util.HashMap;

public class FactDouble extends Fixed1ArgFunction implements FreeRefFunction
{
    public static final FreeRefFunction instance;
    static HashMap<Integer, BigInteger> cache;
    
    @Override
    public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval numberVE) {
        int number;
        try {
            number = OperandResolver.coerceValueToInt(numberVE);
        }
        catch (EvaluationException e) {
            return ErrorEval.VALUE_INVALID;
        }
        if (number < 0) {
            return ErrorEval.NUM_ERROR;
        }
        return new NumberEval((double)factorial(number).longValue());
    }
    
    public static BigInteger factorial(final int n) {
        if (n == 0 || n < 0) {
            return BigInteger.ONE;
        }
        if (FactDouble.cache.containsKey(n)) {
            return FactDouble.cache.get(n);
        }
        final BigInteger result = BigInteger.valueOf(n).multiply(factorial(n - 2));
        FactDouble.cache.put(n, result);
        return result;
    }
    
    @Override
    public ValueEval evaluate(final ValueEval[] args, final OperationEvaluationContext ec) {
        if (args.length != 1) {
            return ErrorEval.VALUE_INVALID;
        }
        return this.evaluate(ec.getRowIndex(), ec.getColumnIndex(), args[0]);
    }
    
    static {
        instance = new FactDouble();
        FactDouble.cache = new HashMap<Integer, BigInteger>();
    }
}

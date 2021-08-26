// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.OperationEvaluationContext;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.RefEval;
import org.apache.poi.ss.formula.eval.ValueEval;

public class Bin2Dec extends Fixed1ArgFunction implements FreeRefFunction
{
    public static final FreeRefFunction instance;
    
    @Override
    public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval numberVE) {
        String number;
        if (numberVE instanceof RefEval) {
            final RefEval re = (RefEval)numberVE;
            number = OperandResolver.coerceValueToString(re.getInnerValueEval(re.getFirstSheetIndex()));
        }
        else {
            number = OperandResolver.coerceValueToString(numberVE);
        }
        if (number.length() > 10) {
            return ErrorEval.NUM_ERROR;
        }
        String unsigned;
        boolean isPositive;
        if (number.length() < 10) {
            unsigned = number;
            isPositive = true;
        }
        else {
            unsigned = number.substring(1);
            isPositive = number.startsWith("0");
        }
        String value;
        try {
            if (isPositive) {
                final int sum = this.getDecimalValue(unsigned);
                value = String.valueOf(sum);
            }
            else {
                final String inverted = toggleBits(unsigned);
                int sum2 = this.getDecimalValue(inverted);
                ++sum2;
                value = "-" + String.valueOf(sum2);
            }
        }
        catch (NumberFormatException e) {
            return ErrorEval.NUM_ERROR;
        }
        return new NumberEval((double)Long.parseLong(value));
    }
    
    private int getDecimalValue(final String unsigned) {
        int sum = 0;
        final int numBits = unsigned.length();
        int power = numBits - 1;
        for (int i = 0; i < numBits; ++i) {
            final int bit = Integer.parseInt(unsigned.substring(i, i + 1));
            final int term = (int)(bit * Math.pow(2.0, power));
            sum += term;
            --power;
        }
        return sum;
    }
    
    private static String toggleBits(final String s) {
        final long i = Long.parseLong(s, 2);
        final long i2 = i ^ (1L << s.length()) - 1L;
        String s2;
        for (s2 = Long.toBinaryString(i2); s2.length() < s.length(); s2 = '0' + s2) {}
        return s2;
    }
    
    @Override
    public ValueEval evaluate(final ValueEval[] args, final OperationEvaluationContext ec) {
        if (args.length != 1) {
            return ErrorEval.VALUE_INVALID;
        }
        return this.evaluate(ec.getRowIndex(), ec.getColumnIndex(), args[0]);
    }
    
    static {
        instance = new Bin2Dec();
    }
}

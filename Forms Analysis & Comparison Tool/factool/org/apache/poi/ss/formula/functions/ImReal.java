// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.OperationEvaluationContext;
import java.util.regex.Matcher;
import org.apache.poi.ss.formula.eval.StringEval;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.ValueEval;

public class ImReal extends Fixed1ArgFunction implements FreeRefFunction
{
    public static final FreeRefFunction instance;
    
    @Override
    public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval inumberVE) {
        ValueEval veText1;
        try {
            veText1 = OperandResolver.getSingleValue(inumberVE, srcRowIndex, srcColumnIndex);
        }
        catch (EvaluationException e) {
            return e.getErrorEval();
        }
        final String iNumber = OperandResolver.coerceValueToString(veText1);
        final Matcher m = Imaginary.COMPLEX_NUMBER_PATTERN.matcher(iNumber);
        final boolean result = m.matches();
        String real = "";
        if (!result) {
            return ErrorEval.NUM_ERROR;
        }
        final String realGroup = m.group(2);
        final boolean hasRealPart = realGroup.length() != 0;
        if (realGroup.length() == 0) {
            return new StringEval(String.valueOf(0));
        }
        if (hasRealPart) {
            String sign = "";
            final String realSign = m.group(1);
            if (realSign.length() != 0 && !realSign.equals("+")) {
                sign = realSign;
            }
            final String groupRealNumber = m.group(2);
            if (groupRealNumber.length() != 0) {
                real = sign + groupRealNumber;
            }
            else {
                real = sign + "1";
            }
        }
        return new StringEval(real);
    }
    
    @Override
    public ValueEval evaluate(final ValueEval[] args, final OperationEvaluationContext ec) {
        if (args.length != 1) {
            return ErrorEval.VALUE_INVALID;
        }
        return this.evaluate(ec.getRowIndex(), ec.getColumnIndex(), args[0]);
    }
    
    static {
        instance = new ImReal();
    }
}

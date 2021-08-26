// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.OperationEvaluationContext;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.ValueEval;

public class Oct2Dec extends Fixed1ArgFunction implements FreeRefFunction
{
    public static final FreeRefFunction instance;
    static final int MAX_NUMBER_OF_PLACES = 10;
    static final int OCTAL_BASE = 8;
    
    @Override
    public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval numberVE) {
        final String octal = OperandResolver.coerceValueToString(numberVE);
        try {
            return new NumberEval(BaseNumberUtils.convertToDecimal(octal, 8, 10));
        }
        catch (IllegalArgumentException e) {
            return ErrorEval.NUM_ERROR;
        }
    }
    
    @Override
    public ValueEval evaluate(final ValueEval[] args, final OperationEvaluationContext ec) {
        if (args.length != 1) {
            return ErrorEval.VALUE_INVALID;
        }
        return this.evaluate(ec.getRowIndex(), ec.getColumnIndex(), args[0]);
    }
    
    static {
        instance = new Oct2Dec();
    }
}

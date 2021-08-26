// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.eval;

import org.apache.poi.ss.formula.functions.Function;
import org.apache.poi.ss.formula.functions.Fixed1ArgFunction;

public final class UnaryMinusEval extends Fixed1ArgFunction
{
    public static final Function instance;
    
    private UnaryMinusEval() {
    }
    
    @Override
    public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval arg0) {
        double d;
        try {
            final ValueEval ve = OperandResolver.getSingleValue(arg0, srcRowIndex, srcColumnIndex);
            d = OperandResolver.coerceValueToDouble(ve);
        }
        catch (EvaluationException e) {
            return e.getErrorEval();
        }
        if (d == 0.0) {
            return NumberEval.ZERO;
        }
        return new NumberEval(-d);
    }
    
    static {
        instance = new UnaryMinusEval();
    }
}

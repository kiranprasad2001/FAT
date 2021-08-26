// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.eval;

import org.apache.poi.ss.formula.functions.Function;
import org.apache.poi.ss.formula.functions.Fixed1ArgFunction;

public final class UnaryPlusEval extends Fixed1ArgFunction
{
    public static final Function instance;
    
    private UnaryPlusEval() {
    }
    
    @Override
    public ValueEval evaluate(final int srcCellRow, final int srcCellCol, final ValueEval arg0) {
        double d;
        try {
            final ValueEval ve = OperandResolver.getSingleValue(arg0, srcCellRow, srcCellCol);
            if (ve instanceof StringEval) {
                return ve;
            }
            d = OperandResolver.coerceValueToDouble(ve);
        }
        catch (EvaluationException e) {
            return e.getErrorEval();
        }
        return new NumberEval(d);
    }
    
    static {
        instance = new UnaryPlusEval();
    }
}

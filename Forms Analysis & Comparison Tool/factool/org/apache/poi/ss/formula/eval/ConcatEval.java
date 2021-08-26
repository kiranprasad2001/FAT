// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.eval;

import org.apache.poi.ss.formula.functions.Function;
import org.apache.poi.ss.formula.functions.Fixed2ArgFunction;

public final class ConcatEval extends Fixed2ArgFunction
{
    public static final Function instance;
    
    private ConcatEval() {
    }
    
    @Override
    public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval arg0, final ValueEval arg1) {
        ValueEval ve0;
        ValueEval ve2;
        try {
            ve0 = OperandResolver.getSingleValue(arg0, srcRowIndex, srcColumnIndex);
            ve2 = OperandResolver.getSingleValue(arg1, srcRowIndex, srcColumnIndex);
        }
        catch (EvaluationException e) {
            return e.getErrorEval();
        }
        final StringBuilder sb = new StringBuilder();
        sb.append(this.getText(ve0));
        sb.append(this.getText(ve2));
        return new StringEval(sb.toString());
    }
    
    private Object getText(final ValueEval ve) {
        if (ve instanceof StringValueEval) {
            final StringValueEval sve = (StringValueEval)ve;
            return sve.getStringValue();
        }
        if (ve == BlankEval.instance) {
            return "";
        }
        throw new IllegalAccessError("Unexpected value type (" + ve.getClass().getName() + ")");
    }
    
    static {
        instance = new ConcatEval();
    }
}

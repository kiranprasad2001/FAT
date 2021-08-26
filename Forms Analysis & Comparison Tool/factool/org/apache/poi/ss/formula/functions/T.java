// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.StringEval;
import org.apache.poi.ss.formula.eval.AreaEval;
import org.apache.poi.ss.formula.eval.RefEval;
import org.apache.poi.ss.formula.eval.ValueEval;

public final class T extends Fixed1ArgFunction
{
    @Override
    public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval arg0) {
        ValueEval arg = arg0;
        if (arg instanceof RefEval) {
            final RefEval re = (RefEval)arg;
            arg = re.getInnerValueEval(re.getFirstSheetIndex());
        }
        else if (arg instanceof AreaEval) {
            arg = ((AreaEval)arg).getRelativeValue(0, 0);
        }
        if (arg instanceof StringEval) {
            return arg;
        }
        if (arg instanceof ErrorEval) {
            return arg;
        }
        return StringEval.EMPTY_INSTANCE;
    }
}

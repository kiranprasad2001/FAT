// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.RefEval;
import org.apache.poi.ss.formula.eval.AreaEval;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.ValueEval;

public final class RowFunc implements Function0Arg, Function1Arg
{
    @Override
    public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex) {
        return new NumberEval(srcRowIndex + 1);
    }
    
    @Override
    public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval arg0) {
        int rnum;
        if (arg0 instanceof AreaEval) {
            rnum = ((AreaEval)arg0).getFirstRow();
        }
        else {
            if (!(arg0 instanceof RefEval)) {
                return ErrorEval.VALUE_INVALID;
            }
            rnum = ((RefEval)arg0).getRow();
        }
        return new NumberEval(rnum + 1);
    }
    
    @Override
    public ValueEval evaluate(final ValueEval[] args, final int srcRowIndex, final int srcColumnIndex) {
        switch (args.length) {
            case 1: {
                return this.evaluate(srcRowIndex, srcColumnIndex, args[0]);
            }
            case 0: {
                return new NumberEval(srcRowIndex + 1);
            }
            default: {
                return ErrorEval.VALUE_INVALID;
            }
        }
    }
}

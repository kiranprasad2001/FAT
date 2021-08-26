// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.ValueEval;

public abstract class Fixed3ArgFunction implements Function3Arg
{
    @Override
    public final ValueEval evaluate(final ValueEval[] args, final int srcRowIndex, final int srcColumnIndex) {
        if (args.length != 3) {
            return ErrorEval.VALUE_INVALID;
        }
        return this.evaluate(srcRowIndex, srcColumnIndex, args[0], args[1], args[2]);
    }
}

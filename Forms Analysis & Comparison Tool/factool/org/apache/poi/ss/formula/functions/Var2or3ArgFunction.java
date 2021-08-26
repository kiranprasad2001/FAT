// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.ValueEval;

abstract class Var2or3ArgFunction implements Function2Arg, Function3Arg
{
    @Override
    public final ValueEval evaluate(final ValueEval[] args, final int srcRowIndex, final int srcColumnIndex) {
        switch (args.length) {
            case 2: {
                return this.evaluate(srcRowIndex, srcColumnIndex, args[0], args[1]);
            }
            case 3: {
                return this.evaluate(srcRowIndex, srcColumnIndex, args[0], args[1], args[2]);
            }
            default: {
                return ErrorEval.VALUE_INVALID;
            }
        }
    }
}

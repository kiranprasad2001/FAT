// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.ValueEval;

public final class Na extends Fixed0ArgFunction
{
    @Override
    public ValueEval evaluate(final int srcCellRow, final int srcCellCol) {
        return ErrorEval.NA;
    }
}

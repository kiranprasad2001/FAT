// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.usermodel.DateUtil;
import java.util.Date;
import org.apache.poi.ss.formula.eval.ValueEval;

public final class Now extends Fixed0ArgFunction
{
    @Override
    public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex) {
        final Date now = new Date(System.currentTimeMillis());
        return new NumberEval(DateUtil.getExcelDate(now));
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.functions;

import java.util.Calendar;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.usermodel.DateUtil;
import java.util.GregorianCalendar;
import org.apache.poi.ss.formula.eval.ValueEval;

public final class Today extends Fixed0ArgFunction
{
    @Override
    public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex) {
        final Calendar now = new GregorianCalendar();
        now.set(now.get(1), now.get(2), now.get(5), 0, 0, 0);
        now.set(14, 0);
        return new NumberEval(DateUtil.getExcelDate(now.getTime()));
    }
}

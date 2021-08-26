// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.functions;

import java.util.Calendar;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.ValueEval;

public final class CalendarFieldFunction extends Fixed1ArgFunction
{
    public static final Function YEAR;
    public static final Function MONTH;
    public static final Function DAY;
    public static final Function HOUR;
    public static final Function MINUTE;
    public static final Function SECOND;
    private final int _dateFieldId;
    
    private CalendarFieldFunction(final int dateFieldId) {
        this._dateFieldId = dateFieldId;
    }
    
    @Override
    public final ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval arg0) {
        double val;
        try {
            final ValueEval ve = OperandResolver.getSingleValue(arg0, srcRowIndex, srcColumnIndex);
            val = OperandResolver.coerceValueToDouble(ve);
        }
        catch (EvaluationException e) {
            return e.getErrorEval();
        }
        if (val < 0.0) {
            return ErrorEval.NUM_ERROR;
        }
        return new NumberEval(this.getCalField(val));
    }
    
    private int getCalField(final double serialDate) {
        if ((int)serialDate == 0) {
            switch (this._dateFieldId) {
                case 1: {
                    return 1900;
                }
                case 2: {
                    return 1;
                }
                case 5: {
                    return 0;
                }
            }
        }
        final Calendar c = DateUtil.getJavaCalendarUTC(serialDate + 5.78125E-6, false);
        int result = c.get(this._dateFieldId);
        if (this._dateFieldId == 2) {
            ++result;
        }
        return result;
    }
    
    static {
        YEAR = new CalendarFieldFunction(1);
        MONTH = new CalendarFieldFunction(2);
        DAY = new CalendarFieldFunction(5);
        HOUR = new CalendarFieldFunction(11);
        MINUTE = new CalendarFieldFunction(12);
        SECOND = new CalendarFieldFunction(13);
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.functions;

import java.util.Calendar;
import org.apache.poi.ss.usermodel.DateUtil;
import java.util.GregorianCalendar;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.ValueEval;

public final class DateFunc extends Fixed3ArgFunction
{
    public static final Function instance;
    
    private DateFunc() {
    }
    
    @Override
    public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval arg0, final ValueEval arg1, final ValueEval arg2) {
        double result;
        try {
            final double d0 = NumericFunction.singleOperandEvaluate(arg0, srcRowIndex, srcColumnIndex);
            final double d2 = NumericFunction.singleOperandEvaluate(arg1, srcRowIndex, srcColumnIndex);
            final double d3 = NumericFunction.singleOperandEvaluate(arg2, srcRowIndex, srcColumnIndex);
            result = evaluate(getYear(d0), (int)(d2 - 1.0), (int)d3);
            NumericFunction.checkValue(result);
        }
        catch (EvaluationException e) {
            return e.getErrorEval();
        }
        return new NumberEval(result);
    }
    
    private static double evaluate(int year, int month, final int pDay) throws EvaluationException {
        if (year < 0) {
            throw new EvaluationException(ErrorEval.VALUE_INVALID);
        }
        while (month < 0) {
            --year;
            month += 12;
        }
        if (year == 1900 && month == 1 && pDay == 29) {
            return 60.0;
        }
        int day = pDay;
        if (year == 1900 && ((month == 0 && day >= 60) || (month == 1 && day >= 30))) {
            --day;
        }
        final Calendar c = new GregorianCalendar();
        c.set(year, month, day, 0, 0, 0);
        c.set(14, 0);
        if (pDay < 0 && c.get(1) == 1900 && month > 1 && c.get(2) < 2) {
            c.add(5, 1);
        }
        final boolean use1904windowing = false;
        return DateUtil.getExcelDate(c.getTime(), use1904windowing);
    }
    
    private static int getYear(final double d) {
        final int year = (int)d;
        if (year < 0) {
            return -1;
        }
        return (year < 1900) ? (1900 + year) : year;
    }
    
    static {
        instance = new DateFunc();
    }
}

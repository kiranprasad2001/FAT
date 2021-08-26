// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.atp;

import java.util.GregorianCalendar;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.ErrorEval;
import java.util.regex.Pattern;
import java.util.Calendar;

public class DateParser
{
    public DateParser instance;
    
    private DateParser() {
        this.instance = new DateParser();
    }
    
    public static Calendar parseDate(final String strVal) throws EvaluationException {
        final String[] parts = Pattern.compile("/").split(strVal);
        if (parts.length != 3) {
            throw new EvaluationException(ErrorEval.VALUE_INVALID);
        }
        String part2 = parts[2];
        final int spacePos = part2.indexOf(32);
        if (spacePos > 0) {
            part2 = part2.substring(0, spacePos);
        }
        int f0;
        int f2;
        int f3;
        try {
            f0 = Integer.parseInt(parts[0]);
            f2 = Integer.parseInt(parts[1]);
            f3 = Integer.parseInt(part2);
        }
        catch (NumberFormatException e) {
            throw new EvaluationException(ErrorEval.VALUE_INVALID);
        }
        if (f0 < 0 || f2 < 0 || f3 < 0 || (f0 > 12 && f2 > 12 && f3 > 12)) {
            throw new EvaluationException(ErrorEval.VALUE_INVALID);
        }
        if (f0 >= 1900 && f0 < 9999) {
            return makeDate(f0, f2, f3);
        }
        throw new RuntimeException("Unable to determine date format for text '" + strVal + "'");
    }
    
    private static Calendar makeDate(final int year, final int month, final int day) throws EvaluationException {
        if (month < 1 || month > 12) {
            throw new EvaluationException(ErrorEval.VALUE_INVALID);
        }
        final Calendar cal = new GregorianCalendar(year, month - 1, 1, 0, 0, 0);
        cal.set(14, 0);
        if (day < 1 || day > cal.getActualMaximum(5)) {
            throw new EvaluationException(ErrorEval.VALUE_INVALID);
        }
        cal.set(5, day);
        return cal;
    }
}

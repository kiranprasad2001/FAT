// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.usermodel.DateUtil;
import java.util.GregorianCalendar;
import java.util.Calendar;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.ValueEval;

public class Days360 extends Var2or3ArgFunction
{
    @Override
    public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval arg0, final ValueEval arg1) {
        double result;
        try {
            final double d0 = NumericFunction.singleOperandEvaluate(arg0, srcRowIndex, srcColumnIndex);
            final double d2 = NumericFunction.singleOperandEvaluate(arg1, srcRowIndex, srcColumnIndex);
            result = evaluate(d0, d2, false);
        }
        catch (EvaluationException e) {
            return e.getErrorEval();
        }
        return new NumberEval(result);
    }
    
    @Override
    public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval arg0, final ValueEval arg1, final ValueEval arg2) {
        double result;
        try {
            final double d0 = NumericFunction.singleOperandEvaluate(arg0, srcRowIndex, srcColumnIndex);
            final double d2 = NumericFunction.singleOperandEvaluate(arg1, srcRowIndex, srcColumnIndex);
            final ValueEval ve = OperandResolver.getSingleValue(arg2, srcRowIndex, srcColumnIndex);
            final Boolean method = OperandResolver.coerceValueToBoolean(ve, false);
            result = evaluate(d0, d2, method != null && method);
        }
        catch (EvaluationException e) {
            return e.getErrorEval();
        }
        return new NumberEval(result);
    }
    
    private static double evaluate(final double d0, final double d1, final boolean method) {
        final Calendar startingDate = getStartingDate(d0);
        final Calendar endingDate = getEndingDateAccordingToStartingDate(d1, startingDate);
        final long startingDay = startingDate.get(2) * 30 + startingDate.get(5);
        final long endingDay = (endingDate.get(1) - startingDate.get(1)) * 360 + endingDate.get(2) * 30 + endingDate.get(5);
        return (double)(endingDay - startingDay);
    }
    
    private static Calendar getDate(final double date) {
        final Calendar processedDate = new GregorianCalendar();
        processedDate.setTime(DateUtil.getJavaDate(date, false));
        return processedDate;
    }
    
    private static Calendar getStartingDate(final double date) {
        final Calendar startingDate = getDate(date);
        if (isLastDayOfMonth(startingDate)) {
            startingDate.set(5, 30);
        }
        return startingDate;
    }
    
    private static Calendar getEndingDateAccordingToStartingDate(final double date, final Calendar startingDate) {
        Calendar endingDate = getDate(date);
        endingDate.setTime(DateUtil.getJavaDate(date, false));
        if (isLastDayOfMonth(endingDate) && startingDate.get(5) < 30) {
            endingDate = getFirstDayOfNextMonth(endingDate);
        }
        return endingDate;
    }
    
    private static boolean isLastDayOfMonth(final Calendar date) {
        final Calendar clone = (Calendar)date.clone();
        clone.add(2, 1);
        clone.add(5, -1);
        final int lastDayOfMonth = clone.get(5);
        return date.get(5) == lastDayOfMonth;
    }
    
    private static Calendar getFirstDayOfNextMonth(final Calendar date) {
        final Calendar newDate = (Calendar)date.clone();
        if (date.get(2) < 11) {
            newDate.set(2, date.get(2) + 1);
        }
        else {
            newDate.set(2, 1);
            newDate.set(1, date.get(1) + 1);
        }
        newDate.set(5, 1);
        return newDate;
    }
}

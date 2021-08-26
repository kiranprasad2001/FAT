// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.atp;

import java.util.List;
import java.util.ArrayList;
import org.apache.poi.ss.formula.eval.AreaEvalBase;
import org.apache.poi.ss.formula.eval.EvaluationException;
import java.util.Calendar;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.formula.eval.StringEval;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.ValueEval;

final class ArgumentsEvaluator
{
    public static final ArgumentsEvaluator instance;
    
    private ArgumentsEvaluator() {
    }
    
    public double evaluateDateArg(final ValueEval arg, final int srcCellRow, final int srcCellCol) throws EvaluationException {
        final ValueEval ve = OperandResolver.getSingleValue(arg, srcCellRow, (short)srcCellCol);
        if (!(ve instanceof StringEval)) {
            return OperandResolver.coerceValueToDouble(ve);
        }
        final String strVal = ((StringEval)ve).getStringValue();
        final Double dVal = OperandResolver.parseDouble(strVal);
        if (dVal != null) {
            return dVal;
        }
        final Calendar date = DateParser.parseDate(strVal);
        return DateUtil.getExcelDate(date, false);
    }
    
    public double[] evaluateDatesArg(final ValueEval arg, final int srcCellRow, final int srcCellCol) throws EvaluationException {
        if (arg == null) {
            return new double[0];
        }
        if (arg instanceof StringEval) {
            return new double[] { this.evaluateDateArg(arg, srcCellRow, srcCellCol) };
        }
        if (arg instanceof AreaEvalBase) {
            final List<Double> valuesList = new ArrayList<Double>();
            final AreaEvalBase area = (AreaEvalBase)arg;
            for (int i = area.getFirstRow(); i <= area.getLastRow(); ++i) {
                for (int j = area.getFirstColumn(); j <= area.getLastColumn(); ++j) {
                    valuesList.add(this.evaluateDateArg(area.getValue(i, j), i, j));
                }
            }
            final double[] values = new double[valuesList.size()];
            for (int k = 0; k < valuesList.size(); ++k) {
                values[k] = valuesList.get(k);
            }
            return values;
        }
        return new double[] { OperandResolver.coerceValueToDouble(arg) };
    }
    
    public double evaluateNumberArg(final ValueEval arg, final int srcCellRow, final int srcCellCol) throws EvaluationException {
        if (arg == null) {
            return 0.0;
        }
        return OperandResolver.coerceValueToDouble(arg);
    }
    
    static {
        instance = new ArgumentsEvaluator();
    }
}

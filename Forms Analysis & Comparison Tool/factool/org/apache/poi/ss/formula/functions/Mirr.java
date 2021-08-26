// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.ErrorEval;

public class Mirr extends MultiOperandNumericFunction
{
    public Mirr() {
        super(false, false);
    }
    
    @Override
    protected int getMaxNumOperands() {
        return 3;
    }
    
    @Override
    protected double evaluate(final double[] values) throws EvaluationException {
        final double financeRate = values[values.length - 1];
        final double reinvestRate = values[values.length - 2];
        final double[] mirrValues = new double[values.length - 2];
        System.arraycopy(values, 0, mirrValues, 0, mirrValues.length);
        boolean mirrValuesAreAllNegatives = true;
        for (final double mirrValue : mirrValues) {
            mirrValuesAreAllNegatives &= (mirrValue < 0.0);
        }
        if (mirrValuesAreAllNegatives) {
            return -1.0;
        }
        boolean mirrValuesAreAllPositives = true;
        for (final double mirrValue2 : mirrValues) {
            mirrValuesAreAllPositives &= (mirrValue2 > 0.0);
        }
        if (mirrValuesAreAllPositives) {
            throw new EvaluationException(ErrorEval.DIV_ZERO);
        }
        return mirr(mirrValues, financeRate, reinvestRate);
    }
    
    private static double mirr(final double[] in, final double financeRate, final double reinvestRate) {
        double value = 0.0;
        final int numOfYears = in.length - 1;
        double pv = 0.0;
        double fv = 0.0;
        int indexN = 0;
        for (final double anIn : in) {
            if (anIn < 0.0) {
                pv += anIn / Math.pow(1.0 + financeRate + reinvestRate, indexN++);
            }
        }
        for (final double anIn : in) {
            if (anIn > 0.0) {
                fv += anIn * Math.pow(1.0 + financeRate, numOfYears - indexN++);
            }
        }
        if (fv != 0.0 && pv != 0.0) {
            value = Math.pow(-fv / pv, 1.0 / numOfYears) - 1.0;
        }
        return value;
    }
}

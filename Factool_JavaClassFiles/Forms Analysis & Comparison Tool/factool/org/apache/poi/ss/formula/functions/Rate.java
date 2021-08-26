// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.ValueEval;

public class Rate implements Function
{
    @Override
    public ValueEval evaluate(final ValueEval[] args, final int srcRowIndex, final int srcColumnIndex) {
        if (args.length < 3) {
            return ErrorEval.VALUE_INVALID;
        }
        double future_val = 0.0;
        double type = 0.0;
        double estimate = 0.1;
        double rate;
        try {
            final ValueEval v1 = OperandResolver.getSingleValue(args[0], srcRowIndex, srcColumnIndex);
            final ValueEval v2 = OperandResolver.getSingleValue(args[1], srcRowIndex, srcColumnIndex);
            final ValueEval v3 = OperandResolver.getSingleValue(args[2], srcRowIndex, srcColumnIndex);
            ValueEval v4 = null;
            if (args.length >= 4) {
                v4 = OperandResolver.getSingleValue(args[3], srcRowIndex, srcColumnIndex);
            }
            ValueEval v5 = null;
            if (args.length >= 5) {
                v5 = OperandResolver.getSingleValue(args[4], srcRowIndex, srcColumnIndex);
            }
            ValueEval v6 = null;
            if (args.length >= 6) {
                v6 = OperandResolver.getSingleValue(args[5], srcRowIndex, srcColumnIndex);
            }
            final double periods = OperandResolver.coerceValueToDouble(v1);
            final double payment = OperandResolver.coerceValueToDouble(v2);
            final double present_val = OperandResolver.coerceValueToDouble(v3);
            if (args.length >= 4) {
                future_val = OperandResolver.coerceValueToDouble(v4);
            }
            if (args.length >= 5) {
                type = OperandResolver.coerceValueToDouble(v5);
            }
            if (args.length >= 6) {
                estimate = OperandResolver.coerceValueToDouble(v6);
            }
            rate = this.calculateRate(periods, payment, present_val, future_val, type, estimate);
            checkValue(rate);
        }
        catch (EvaluationException e) {
            e.printStackTrace();
            return e.getErrorEval();
        }
        return new NumberEval(rate);
    }
    
    private double calculateRate(final double nper, final double pmt, final double pv, final double fv, final double type, final double guess) {
        final int FINANCIAL_MAX_ITERATIONS = 20;
        final double FINANCIAL_PRECISION = 1.0E-7;
        double x1 = 0.0;
        double f = 0.0;
        double i = 0.0;
        double rate = guess;
        if (Math.abs(rate) < FINANCIAL_PRECISION) {
            final double y = pv * (1.0 + nper * rate) + pmt * (1.0 + rate * type) * nper + fv;
        }
        else {
            f = Math.exp(nper * Math.log(1.0 + rate));
            final double y = pv * f + pmt * (1.0 / rate + type) * (f - 1.0) + fv;
        }
        double y2 = pv + pmt * nper + fv;
        double y3 = pv * f + pmt * (1.0 / rate + type) * (f - 1.0) + fv;
        double x2 = i = 0.0;
        x1 = rate;
        while (Math.abs(y2 - y3) > FINANCIAL_PRECISION && i < FINANCIAL_MAX_ITERATIONS) {
            rate = (y3 * x2 - y2 * x1) / (y3 - y2);
            x2 = x1;
            x1 = rate;
            double y;
            if (Math.abs(rate) < FINANCIAL_PRECISION) {
                y = pv * (1.0 + nper * rate) + pmt * (1.0 + rate * type) * nper + fv;
            }
            else {
                f = Math.exp(nper * Math.log(1.0 + rate));
                y = pv * f + pmt * (1.0 / rate + type) * (f - 1.0) + fv;
            }
            y2 = y3;
            y3 = y;
            ++i;
        }
        return rate;
    }
    
    static final void checkValue(final double result) throws EvaluationException {
        if (Double.isNaN(result) || Double.isInfinite(result)) {
            throw new EvaluationException(ErrorEval.NUM_ERROR);
        }
    }
}

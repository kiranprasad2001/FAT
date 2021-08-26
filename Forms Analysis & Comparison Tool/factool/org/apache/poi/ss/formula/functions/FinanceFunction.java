// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.eval.BoolEval;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.ValueEval;

public abstract class FinanceFunction implements Function3Arg, Function4Arg
{
    private static final ValueEval DEFAULT_ARG3;
    private static final ValueEval DEFAULT_ARG4;
    public static final Function FV;
    public static final Function NPER;
    public static final Function PMT;
    public static final Function PV;
    
    protected FinanceFunction() {
    }
    
    @Override
    public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval arg0, final ValueEval arg1, final ValueEval arg2) {
        return this.evaluate(srcRowIndex, srcColumnIndex, arg0, arg1, arg2, FinanceFunction.DEFAULT_ARG3);
    }
    
    @Override
    public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval arg0, final ValueEval arg1, final ValueEval arg2, final ValueEval arg3) {
        return this.evaluate(srcRowIndex, srcColumnIndex, arg0, arg1, arg2, arg3, FinanceFunction.DEFAULT_ARG4);
    }
    
    public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval arg0, final ValueEval arg1, final ValueEval arg2, final ValueEval arg3, final ValueEval arg4) {
        double result;
        try {
            final double d0 = NumericFunction.singleOperandEvaluate(arg0, srcRowIndex, srcColumnIndex);
            final double d2 = NumericFunction.singleOperandEvaluate(arg1, srcRowIndex, srcColumnIndex);
            final double d3 = NumericFunction.singleOperandEvaluate(arg2, srcRowIndex, srcColumnIndex);
            final double d4 = NumericFunction.singleOperandEvaluate(arg3, srcRowIndex, srcColumnIndex);
            final double d5 = NumericFunction.singleOperandEvaluate(arg4, srcRowIndex, srcColumnIndex);
            result = this.evaluate(d0, d2, d3, d4, d5 != 0.0);
            NumericFunction.checkValue(result);
        }
        catch (EvaluationException e) {
            return e.getErrorEval();
        }
        return new NumberEval(result);
    }
    
    @Override
    public ValueEval evaluate(final ValueEval[] args, final int srcRowIndex, final int srcColumnIndex) {
        switch (args.length) {
            case 3: {
                return this.evaluate(srcRowIndex, srcColumnIndex, args[0], args[1], args[2], FinanceFunction.DEFAULT_ARG3, FinanceFunction.DEFAULT_ARG4);
            }
            case 4: {
                return this.evaluate(srcRowIndex, srcColumnIndex, args[0], args[1], args[2], args[3], FinanceFunction.DEFAULT_ARG4);
            }
            case 5: {
                return this.evaluate(srcRowIndex, srcColumnIndex, args[0], args[1], args[2], args[3], args[4]);
            }
            default: {
                return ErrorEval.VALUE_INVALID;
            }
        }
    }
    
    protected double evaluate(final double[] ds) throws EvaluationException {
        double arg3 = 0.0;
        double arg4 = 0.0;
        switch (ds.length) {
            case 5: {
                arg4 = ds[4];
            }
            case 4: {
                arg3 = ds[3];
            }
            case 3: {
                return this.evaluate(ds[0], ds[1], ds[2], arg3, arg4 != 0.0);
            }
            default: {
                throw new IllegalStateException("Wrong number of arguments");
            }
        }
    }
    
    protected abstract double evaluate(final double p0, final double p1, final double p2, final double p3, final boolean p4) throws EvaluationException;
    
    static {
        DEFAULT_ARG3 = NumberEval.ZERO;
        DEFAULT_ARG4 = BoolEval.FALSE;
        FV = new FinanceFunction() {
            @Override
            protected double evaluate(final double rate, final double arg1, final double arg2, final double arg3, final boolean type) {
                return FinanceLib.fv(rate, arg1, arg2, arg3, type);
            }
        };
        NPER = new FinanceFunction() {
            @Override
            protected double evaluate(final double rate, final double arg1, final double arg2, final double arg3, final boolean type) {
                return FinanceLib.nper(rate, arg1, arg2, arg3, type);
            }
        };
        PMT = new FinanceFunction() {
            @Override
            protected double evaluate(final double rate, final double arg1, final double arg2, final double arg3, final boolean type) {
                return FinanceLib.pmt(rate, arg1, arg2, arg3, type);
            }
        };
        PV = new FinanceFunction() {
            @Override
            protected double evaluate(final double rate, final double arg1, final double arg2, final double arg3, final boolean type) {
                return FinanceLib.pv(rate, arg1, arg2, arg3, type);
            }
        };
    }
}

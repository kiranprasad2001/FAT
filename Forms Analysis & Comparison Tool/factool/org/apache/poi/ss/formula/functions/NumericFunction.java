// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.eval.BoolEval;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.eval.NumberEval;

public abstract class NumericFunction implements Function
{
    static final double ZERO = 0.0;
    static final double TEN = 10.0;
    static final double LOG_10_TO_BASE_e;
    public static final Function ABS;
    public static final Function ACOS;
    public static final Function ACOSH;
    public static final Function ASIN;
    public static final Function ASINH;
    public static final Function ATAN;
    public static final Function ATANH;
    public static final Function COS;
    public static final Function COSH;
    public static final Function DEGREES;
    static final NumberEval DOLLAR_ARG2_DEFAULT;
    public static final Function DOLLAR;
    public static final Function EXP;
    public static final Function FACT;
    public static final Function INT;
    public static final Function LN;
    public static final Function LOG10;
    public static final Function RADIANS;
    public static final Function SIGN;
    public static final Function SIN;
    public static final Function SINH;
    public static final Function SQRT;
    public static final Function TAN;
    public static final Function TANH;
    public static final Function ATAN2;
    public static final Function CEILING;
    public static final Function COMBIN;
    public static final Function FLOOR;
    public static final Function MOD;
    public static final Function POWER;
    public static final Function ROUND;
    public static final Function ROUNDDOWN;
    public static final Function ROUNDUP;
    static final NumberEval TRUNC_ARG2_DEFAULT;
    public static final Function TRUNC;
    public static final Function LOG;
    static final NumberEval PI_EVAL;
    public static final Function PI;
    public static final Function RAND;
    public static final Function POISSON;
    
    protected static final double singleOperandEvaluate(final ValueEval arg, final int srcRowIndex, final int srcColumnIndex) throws EvaluationException {
        if (arg == null) {
            throw new IllegalArgumentException("arg must not be null");
        }
        final ValueEval ve = OperandResolver.getSingleValue(arg, srcRowIndex, srcColumnIndex);
        final double result = OperandResolver.coerceValueToDouble(ve);
        checkValue(result);
        return result;
    }
    
    public static final void checkValue(final double result) throws EvaluationException {
        if (Double.isNaN(result) || Double.isInfinite(result)) {
            throw new EvaluationException(ErrorEval.NUM_ERROR);
        }
    }
    
    @Override
    public final ValueEval evaluate(final ValueEval[] args, final int srcCellRow, final int srcCellCol) {
        double result;
        try {
            result = this.eval(args, srcCellRow, srcCellCol);
            checkValue(result);
        }
        catch (EvaluationException e) {
            return e.getErrorEval();
        }
        return new NumberEval(result);
    }
    
    protected abstract double eval(final ValueEval[] p0, final int p1, final int p2) throws EvaluationException;
    
    static {
        LOG_10_TO_BASE_e = Math.log(10.0);
        ABS = new OneArg() {
            @Override
            protected double evaluate(final double d) {
                return Math.abs(d);
            }
        };
        ACOS = new OneArg() {
            @Override
            protected double evaluate(final double d) {
                return Math.acos(d);
            }
        };
        ACOSH = new OneArg() {
            @Override
            protected double evaluate(final double d) {
                return MathX.acosh(d);
            }
        };
        ASIN = new OneArg() {
            @Override
            protected double evaluate(final double d) {
                return Math.asin(d);
            }
        };
        ASINH = new OneArg() {
            @Override
            protected double evaluate(final double d) {
                return MathX.asinh(d);
            }
        };
        ATAN = new OneArg() {
            @Override
            protected double evaluate(final double d) {
                return Math.atan(d);
            }
        };
        ATANH = new OneArg() {
            @Override
            protected double evaluate(final double d) {
                return MathX.atanh(d);
            }
        };
        COS = new OneArg() {
            @Override
            protected double evaluate(final double d) {
                return Math.cos(d);
            }
        };
        COSH = new OneArg() {
            @Override
            protected double evaluate(final double d) {
                return MathX.cosh(d);
            }
        };
        DEGREES = new OneArg() {
            @Override
            protected double evaluate(final double d) {
                return Math.toDegrees(d);
            }
        };
        DOLLAR_ARG2_DEFAULT = new NumberEval(2.0);
        DOLLAR = new Var1or2ArgFunction() {
            @Override
            public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval arg0) {
                return this.evaluate(srcRowIndex, srcColumnIndex, arg0, NumericFunction.DOLLAR_ARG2_DEFAULT);
            }
            
            @Override
            public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval arg0, final ValueEval arg1) {
                double val;
                double d1;
                try {
                    val = NumericFunction.singleOperandEvaluate(arg0, srcRowIndex, srcColumnIndex);
                    d1 = NumericFunction.singleOperandEvaluate(arg1, srcRowIndex, srcColumnIndex);
                }
                catch (EvaluationException e) {
                    return e.getErrorEval();
                }
                final int nPlaces = (int)d1;
                if (nPlaces > 127) {
                    return ErrorEval.VALUE_INVALID;
                }
                return new NumberEval(val);
            }
        };
        EXP = new OneArg() {
            @Override
            protected double evaluate(final double d) {
                return Math.pow(2.718281828459045, d);
            }
        };
        FACT = new OneArg() {
            @Override
            protected double evaluate(final double d) {
                return MathX.factorial((int)d);
            }
        };
        INT = new OneArg() {
            @Override
            protected double evaluate(final double d) {
                return (double)Math.round(d - 0.5);
            }
        };
        LN = new OneArg() {
            @Override
            protected double evaluate(final double d) {
                return Math.log(d);
            }
        };
        LOG10 = new OneArg() {
            @Override
            protected double evaluate(final double d) {
                return Math.log(d) / NumericFunction.LOG_10_TO_BASE_e;
            }
        };
        RADIANS = new OneArg() {
            @Override
            protected double evaluate(final double d) {
                return Math.toRadians(d);
            }
        };
        SIGN = new OneArg() {
            @Override
            protected double evaluate(final double d) {
                return MathX.sign(d);
            }
        };
        SIN = new OneArg() {
            @Override
            protected double evaluate(final double d) {
                return Math.sin(d);
            }
        };
        SINH = new OneArg() {
            @Override
            protected double evaluate(final double d) {
                return MathX.sinh(d);
            }
        };
        SQRT = new OneArg() {
            @Override
            protected double evaluate(final double d) {
                return Math.sqrt(d);
            }
        };
        TAN = new OneArg() {
            @Override
            protected double evaluate(final double d) {
                return Math.tan(d);
            }
        };
        TANH = new OneArg() {
            @Override
            protected double evaluate(final double d) {
                return MathX.tanh(d);
            }
        };
        ATAN2 = new TwoArg() {
            @Override
            protected double evaluate(final double d0, final double d1) throws EvaluationException {
                if (d0 == 0.0 && d1 == 0.0) {
                    throw new EvaluationException(ErrorEval.DIV_ZERO);
                }
                return Math.atan2(d1, d0);
            }
        };
        CEILING = new TwoArg() {
            @Override
            protected double evaluate(final double d0, final double d1) {
                return MathX.ceiling(d0, d1);
            }
        };
        COMBIN = new TwoArg() {
            @Override
            protected double evaluate(final double d0, final double d1) throws EvaluationException {
                if (d0 > 2.147483647E9 || d1 > 2.147483647E9) {
                    throw new EvaluationException(ErrorEval.NUM_ERROR);
                }
                return MathX.nChooseK((int)d0, (int)d1);
            }
        };
        FLOOR = new TwoArg() {
            @Override
            protected double evaluate(final double d0, final double d1) throws EvaluationException {
                if (d1 != 0.0) {
                    return MathX.floor(d0, d1);
                }
                if (d0 == 0.0) {
                    return 0.0;
                }
                throw new EvaluationException(ErrorEval.DIV_ZERO);
            }
        };
        MOD = new TwoArg() {
            @Override
            protected double evaluate(final double d0, final double d1) throws EvaluationException {
                if (d1 == 0.0) {
                    throw new EvaluationException(ErrorEval.DIV_ZERO);
                }
                return MathX.mod(d0, d1);
            }
        };
        POWER = new TwoArg() {
            @Override
            protected double evaluate(final double d0, final double d1) {
                return Math.pow(d0, d1);
            }
        };
        ROUND = new TwoArg() {
            @Override
            protected double evaluate(final double d0, final double d1) {
                return MathX.round(d0, (int)d1);
            }
        };
        ROUNDDOWN = new TwoArg() {
            @Override
            protected double evaluate(final double d0, final double d1) {
                return MathX.roundDown(d0, (int)d1);
            }
        };
        ROUNDUP = new TwoArg() {
            @Override
            protected double evaluate(final double d0, final double d1) {
                return MathX.roundUp(d0, (int)d1);
            }
        };
        TRUNC_ARG2_DEFAULT = new NumberEval(0.0);
        TRUNC = new Var1or2ArgFunction() {
            @Override
            public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval arg0) {
                return this.evaluate(srcRowIndex, srcColumnIndex, arg0, NumericFunction.TRUNC_ARG2_DEFAULT);
            }
            
            @Override
            public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval arg0, final ValueEval arg1) {
                double result;
                try {
                    final double d0 = NumericFunction.singleOperandEvaluate(arg0, srcRowIndex, srcColumnIndex);
                    final double d2 = NumericFunction.singleOperandEvaluate(arg1, srcRowIndex, srcColumnIndex);
                    final double multi = Math.pow(10.0, d2);
                    if (d0 < 0.0) {
                        result = -Math.floor(-d0 * multi) / multi;
                    }
                    else {
                        result = Math.floor(d0 * multi) / multi;
                    }
                    NumericFunction.checkValue(result);
                }
                catch (EvaluationException e) {
                    return e.getErrorEval();
                }
                return new NumberEval(result);
            }
        };
        LOG = new Log();
        PI_EVAL = new NumberEval(3.141592653589793);
        PI = new Fixed0ArgFunction() {
            @Override
            public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex) {
                return NumericFunction.PI_EVAL;
            }
        };
        RAND = new Fixed0ArgFunction() {
            @Override
            public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex) {
                return new NumberEval(Math.random());
            }
        };
        POISSON = new Fixed3ArgFunction() {
            private static final double DEFAULT_RETURN_RESULT = 1.0;
            private final long[] FACTORIALS = { 1L, 1L, 2L, 6L, 24L, 120L, 720L, 5040L, 40320L, 362880L, 3628800L, 39916800L, 479001600L, 6227020800L, 87178291200L, 1307674368000L, 20922789888000L, 355687428096000L, 6402373705728000L, 121645100408832000L, 2432902008176640000L };
            
            private boolean isDefaultResult(final double x, final double mean) {
                return x == 0.0 && mean == 0.0;
            }
            
            private boolean checkArgument(final double aDouble) throws EvaluationException {
                NumericFunction.checkValue(aDouble);
                if (aDouble < 0.0) {
                    throw new EvaluationException(ErrorEval.NUM_ERROR);
                }
                return true;
            }
            
            private double probability(final int k, final double lambda) {
                return Math.pow(lambda, k) * Math.exp(-lambda) / this.factorial(k);
            }
            
            private double cumulativeProbability(final int x, final double lambda) {
                double result = 0.0;
                for (int k = 0; k <= x; ++k) {
                    result += this.probability(k, lambda);
                }
                return result;
            }
            
            public long factorial(final int n) {
                if (n < 0 || n > 20) {
                    throw new IllegalArgumentException("Valid argument should be in the range [0..20]");
                }
                return this.FACTORIALS[n];
            }
            
            @Override
            public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval arg0, final ValueEval arg1, final ValueEval arg2) {
                double mean = 0.0;
                double x = 0.0;
                final boolean cumulative = ((BoolEval)arg2).getBooleanValue();
                double result = 0.0;
                try {
                    x = NumericFunction.singleOperandEvaluate(arg0, srcRowIndex, srcColumnIndex);
                    mean = NumericFunction.singleOperandEvaluate(arg1, srcRowIndex, srcColumnIndex);
                    if (this.isDefaultResult(x, mean)) {
                        return new NumberEval(1.0);
                    }
                    this.checkArgument(x);
                    this.checkArgument(mean);
                    if (cumulative) {
                        result = this.cumulativeProbability((int)x, mean);
                    }
                    else {
                        result = this.probability((int)x, mean);
                    }
                    NumericFunction.checkValue(result);
                }
                catch (EvaluationException e) {
                    return e.getErrorEval();
                }
                return new NumberEval(result);
            }
        };
    }
    
    public abstract static class OneArg extends Fixed1ArgFunction
    {
        protected OneArg() {
        }
        
        @Override
        public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval arg0) {
            double result;
            try {
                final double d = NumericFunction.singleOperandEvaluate(arg0, srcRowIndex, srcColumnIndex);
                result = this.evaluate(d);
                NumericFunction.checkValue(result);
            }
            catch (EvaluationException e) {
                return e.getErrorEval();
            }
            return new NumberEval(result);
        }
        
        protected final double eval(final ValueEval[] args, final int srcCellRow, final int srcCellCol) throws EvaluationException {
            if (args.length != 1) {
                throw new EvaluationException(ErrorEval.VALUE_INVALID);
            }
            final double d = NumericFunction.singleOperandEvaluate(args[0], srcCellRow, srcCellCol);
            return this.evaluate(d);
        }
        
        protected abstract double evaluate(final double p0) throws EvaluationException;
    }
    
    public abstract static class TwoArg extends Fixed2ArgFunction
    {
        protected TwoArg() {
        }
        
        @Override
        public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval arg0, final ValueEval arg1) {
            double result;
            try {
                final double d0 = NumericFunction.singleOperandEvaluate(arg0, srcRowIndex, srcColumnIndex);
                final double d2 = NumericFunction.singleOperandEvaluate(arg1, srcRowIndex, srcColumnIndex);
                result = this.evaluate(d0, d2);
                NumericFunction.checkValue(result);
            }
            catch (EvaluationException e) {
                return e.getErrorEval();
            }
            return new NumberEval(result);
        }
        
        protected abstract double evaluate(final double p0, final double p1) throws EvaluationException;
    }
    
    private static final class Log extends Var1or2ArgFunction
    {
        public Log() {
        }
        
        @Override
        public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval arg0) {
            double result;
            try {
                final double d0 = NumericFunction.singleOperandEvaluate(arg0, srcRowIndex, srcColumnIndex);
                result = Math.log(d0) / NumericFunction.LOG_10_TO_BASE_e;
                NumericFunction.checkValue(result);
            }
            catch (EvaluationException e) {
                return e.getErrorEval();
            }
            return new NumberEval(result);
        }
        
        @Override
        public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval arg0, final ValueEval arg1) {
            double result;
            try {
                final double d0 = NumericFunction.singleOperandEvaluate(arg0, srcRowIndex, srcColumnIndex);
                final double d2 = NumericFunction.singleOperandEvaluate(arg1, srcRowIndex, srcColumnIndex);
                final double logE = Math.log(d0);
                final double base = d2;
                if (base == 2.718281828459045) {
                    result = logE;
                }
                else {
                    result = logE / Math.log(base);
                }
                NumericFunction.checkValue(result);
            }
            catch (EvaluationException e) {
                return e.getErrorEval();
            }
            return new NumberEval(result);
        }
    }
}

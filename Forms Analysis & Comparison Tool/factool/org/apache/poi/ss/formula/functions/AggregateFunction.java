// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;

public abstract class AggregateFunction extends MultiOperandNumericFunction
{
    public static final Function AVEDEV;
    public static final Function AVERAGE;
    public static final Function DEVSQ;
    public static final Function LARGE;
    public static final Function MAX;
    public static final Function MEDIAN;
    public static final Function MIN;
    public static final Function PERCENTILE;
    public static final Function PRODUCT;
    public static final Function SMALL;
    public static final Function STDEV;
    public static final Function SUM;
    public static final Function SUMSQ;
    public static final Function VAR;
    public static final Function VARP;
    
    protected AggregateFunction() {
        super(false, false);
    }
    
    static Function subtotalInstance(final Function func) {
        final AggregateFunction arg = (AggregateFunction)func;
        return new AggregateFunction() {
            @Override
            protected double evaluate(final double[] values) throws EvaluationException {
                return arg.evaluate(values);
            }
            
            @Override
            public boolean isSubtotalCounted() {
                return false;
            }
        };
    }
    
    static {
        AVEDEV = new AggregateFunction() {
            @Override
            protected double evaluate(final double[] values) {
                return StatsLib.avedev(values);
            }
        };
        AVERAGE = new AggregateFunction() {
            @Override
            protected double evaluate(final double[] values) throws EvaluationException {
                if (values.length < 1) {
                    throw new EvaluationException(ErrorEval.DIV_ZERO);
                }
                return MathX.average(values);
            }
        };
        DEVSQ = new AggregateFunction() {
            @Override
            protected double evaluate(final double[] values) {
                return StatsLib.devsq(values);
            }
        };
        LARGE = new LargeSmall(true);
        MAX = new AggregateFunction() {
            @Override
            protected double evaluate(final double[] values) {
                return (values.length > 0) ? MathX.max(values) : 0.0;
            }
        };
        MEDIAN = new AggregateFunction() {
            @Override
            protected double evaluate(final double[] values) {
                return StatsLib.median(values);
            }
        };
        MIN = new AggregateFunction() {
            @Override
            protected double evaluate(final double[] values) {
                return (values.length > 0) ? MathX.min(values) : 0.0;
            }
        };
        PERCENTILE = new Percentile();
        PRODUCT = new AggregateFunction() {
            @Override
            protected double evaluate(final double[] values) {
                return MathX.product(values);
            }
        };
        SMALL = new LargeSmall(false);
        STDEV = new AggregateFunction() {
            @Override
            protected double evaluate(final double[] values) throws EvaluationException {
                if (values.length < 1) {
                    throw new EvaluationException(ErrorEval.DIV_ZERO);
                }
                return StatsLib.stdev(values);
            }
        };
        SUM = new AggregateFunction() {
            @Override
            protected double evaluate(final double[] values) {
                return MathX.sum(values);
            }
        };
        SUMSQ = new AggregateFunction() {
            @Override
            protected double evaluate(final double[] values) {
                return MathX.sumsq(values);
            }
        };
        VAR = new AggregateFunction() {
            @Override
            protected double evaluate(final double[] values) throws EvaluationException {
                if (values.length < 1) {
                    throw new EvaluationException(ErrorEval.DIV_ZERO);
                }
                return StatsLib.var(values);
            }
        };
        VARP = new AggregateFunction() {
            @Override
            protected double evaluate(final double[] values) throws EvaluationException {
                if (values.length < 1) {
                    throw new EvaluationException(ErrorEval.DIV_ZERO);
                }
                return StatsLib.varp(values);
            }
        };
    }
    
    private static final class LargeSmall extends Fixed2ArgFunction
    {
        private final boolean _isLarge;
        
        protected LargeSmall(final boolean isLarge) {
            this._isLarge = isLarge;
        }
        
        @Override
        public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval arg0, final ValueEval arg1) {
            double dn;
            try {
                final ValueEval ve1 = OperandResolver.getSingleValue(arg1, srcRowIndex, srcColumnIndex);
                dn = OperandResolver.coerceValueToDouble(ve1);
            }
            catch (EvaluationException e2) {
                return ErrorEval.VALUE_INVALID;
            }
            if (dn < 1.0) {
                return ErrorEval.NUM_ERROR;
            }
            final int k = (int)Math.ceil(dn);
            double result;
            try {
                final double[] ds = ValueCollector.collectValues(arg0);
                if (k > ds.length) {
                    return ErrorEval.NUM_ERROR;
                }
                result = (this._isLarge ? StatsLib.kthLargest(ds, k) : StatsLib.kthSmallest(ds, k));
                NumericFunction.checkValue(result);
            }
            catch (EvaluationException e) {
                return e.getErrorEval();
            }
            return new NumberEval(result);
        }
    }
    
    private static final class Percentile extends Fixed2ArgFunction
    {
        protected Percentile() {
        }
        
        @Override
        public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval arg0, final ValueEval arg1) {
            double dn;
            try {
                final ValueEval ve1 = OperandResolver.getSingleValue(arg1, srcRowIndex, srcColumnIndex);
                dn = OperandResolver.coerceValueToDouble(ve1);
            }
            catch (EvaluationException e2) {
                return ErrorEval.VALUE_INVALID;
            }
            if (dn < 0.0 || dn > 1.0) {
                return ErrorEval.NUM_ERROR;
            }
            double result;
            try {
                final double[] ds = ValueCollector.collectValues(arg0);
                final int N = ds.length;
                if (N == 0 || N > 8191) {
                    return ErrorEval.NUM_ERROR;
                }
                final double n = (N - 1) * dn + 1.0;
                if (n == 1.0) {
                    result = StatsLib.kthSmallest(ds, 1);
                }
                else if (n == N) {
                    result = StatsLib.kthLargest(ds, 1);
                }
                else {
                    final int k = (int)n;
                    final double d = n - k;
                    result = StatsLib.kthSmallest(ds, k) + d * (StatsLib.kthSmallest(ds, k + 1) - StatsLib.kthSmallest(ds, k));
                }
                NumericFunction.checkValue(result);
            }
            catch (EvaluationException e) {
                return e.getErrorEval();
            }
            return new NumberEval(result);
        }
    }
    
    static final class ValueCollector extends MultiOperandNumericFunction
    {
        private static final ValueCollector instance;
        
        public ValueCollector() {
            super(false, false);
        }
        
        public static double[] collectValues(final ValueEval... operands) throws EvaluationException {
            return ValueCollector.instance.getNumberArray(operands);
        }
        
        @Override
        protected double evaluate(final double[] values) {
            throw new IllegalStateException("should not be called");
        }
        
        static {
            instance = new ValueCollector();
        }
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.eval.BlankEval;
import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.StringValueEval;
import org.apache.poi.ss.formula.eval.NumericValueEval;
import org.apache.poi.ss.formula.eval.BoolEval;
import org.apache.poi.ss.formula.eval.RefEval;
import org.apache.poi.ss.formula.TwoDEval;
import org.apache.poi.ss.formula.ThreeDEval;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.ValueEval;

public abstract class MultiOperandNumericFunction implements Function
{
    private final boolean _isReferenceBoolCounted;
    private final boolean _isBlankCounted;
    static final double[] EMPTY_DOUBLE_ARRAY;
    private static final int DEFAULT_MAX_NUM_OPERANDS = 30;
    
    protected MultiOperandNumericFunction(final boolean isReferenceBoolCounted, final boolean isBlankCounted) {
        this._isReferenceBoolCounted = isReferenceBoolCounted;
        this._isBlankCounted = isBlankCounted;
    }
    
    @Override
    public final ValueEval evaluate(final ValueEval[] args, final int srcCellRow, final int srcCellCol) {
        double d;
        try {
            final double[] values = this.getNumberArray(args);
            d = this.evaluate(values);
        }
        catch (EvaluationException e) {
            return e.getErrorEval();
        }
        if (Double.isNaN(d) || Double.isInfinite(d)) {
            return ErrorEval.NUM_ERROR;
        }
        return new NumberEval(d);
    }
    
    protected abstract double evaluate(final double[] p0) throws EvaluationException;
    
    protected int getMaxNumOperands() {
        return 30;
    }
    
    protected final double[] getNumberArray(final ValueEval[] operands) throws EvaluationException {
        if (operands.length > this.getMaxNumOperands()) {
            throw EvaluationException.invalidValue();
        }
        final DoubleList retval = new DoubleList();
        for (int i = 0, iSize = operands.length; i < iSize; ++i) {
            this.collectValues(operands[i], retval);
        }
        return retval.toArray();
    }
    
    public boolean isSubtotalCounted() {
        return true;
    }
    
    private void collectValues(final ValueEval operand, final DoubleList temp) throws EvaluationException {
        if (operand instanceof ThreeDEval) {
            final ThreeDEval ae = (ThreeDEval)operand;
            for (int sIx = ae.getFirstSheetIndex(); sIx <= ae.getLastSheetIndex(); ++sIx) {
                final int width = ae.getWidth();
                for (int height = ae.getHeight(), rrIx = 0; rrIx < height; ++rrIx) {
                    for (int rcIx = 0; rcIx < width; ++rcIx) {
                        final ValueEval ve = ae.getValue(sIx, rrIx, rcIx);
                        if (this.isSubtotalCounted() || !ae.isSubTotal(rrIx, rcIx)) {
                            this.collectValue(ve, true, temp);
                        }
                    }
                }
            }
            return;
        }
        if (operand instanceof TwoDEval) {
            final TwoDEval ae2 = (TwoDEval)operand;
            final int width2 = ae2.getWidth();
            for (int height2 = ae2.getHeight(), rrIx2 = 0; rrIx2 < height2; ++rrIx2) {
                for (int rcIx2 = 0; rcIx2 < width2; ++rcIx2) {
                    final ValueEval ve2 = ae2.getValue(rrIx2, rcIx2);
                    if (this.isSubtotalCounted() || !ae2.isSubTotal(rrIx2, rcIx2)) {
                        this.collectValue(ve2, true, temp);
                    }
                }
            }
            return;
        }
        if (operand instanceof RefEval) {
            final RefEval re = (RefEval)operand;
            for (int sIx = re.getFirstSheetIndex(); sIx <= re.getLastSheetIndex(); ++sIx) {
                this.collectValue(re.getInnerValueEval(sIx), true, temp);
            }
            return;
        }
        this.collectValue(operand, false, temp);
    }
    
    private void collectValue(final ValueEval ve, final boolean isViaReference, final DoubleList temp) throws EvaluationException {
        if (ve == null) {
            throw new IllegalArgumentException("ve must not be null");
        }
        if (ve instanceof BoolEval) {
            if (!isViaReference || this._isReferenceBoolCounted) {
                final BoolEval boolEval = (BoolEval)ve;
                temp.add(boolEval.getNumberValue());
            }
            return;
        }
        if (ve instanceof NumericValueEval) {
            final NumericValueEval ne = (NumericValueEval)ve;
            temp.add(ne.getNumberValue());
            return;
        }
        if (ve instanceof StringValueEval) {
            if (isViaReference) {
                return;
            }
            final String s = ((StringValueEval)ve).getStringValue();
            final Double d = OperandResolver.parseDouble(s);
            if (d == null) {
                throw new EvaluationException(ErrorEval.VALUE_INVALID);
            }
            temp.add(d);
        }
        else {
            if (ve instanceof ErrorEval) {
                throw new EvaluationException((ErrorEval)ve);
            }
            if (ve == BlankEval.instance) {
                if (this._isBlankCounted) {
                    temp.add(0.0);
                }
                return;
            }
            throw new RuntimeException("Invalid ValueEval type passed for conversion: (" + ve.getClass() + ")");
        }
    }
    
    static {
        EMPTY_DOUBLE_ARRAY = new double[0];
    }
    
    private static class DoubleList
    {
        private double[] _array;
        private int _count;
        
        public DoubleList() {
            this._array = new double[8];
            this._count = 0;
        }
        
        public double[] toArray() {
            if (this._count < 1) {
                return MultiOperandNumericFunction.EMPTY_DOUBLE_ARRAY;
            }
            final double[] result = new double[this._count];
            System.arraycopy(this._array, 0, result, 0, this._count);
            return result;
        }
        
        private void ensureCapacity(final int reqSize) {
            if (reqSize > this._array.length) {
                final int newSize = reqSize * 3 / 2;
                final double[] newArr = new double[newSize];
                System.arraycopy(this._array, 0, newArr, 0, this._count);
                this._array = newArr;
            }
        }
        
        public void add(final double value) {
            this.ensureCapacity(this._count + 1);
            this._array[this._count] = value;
            ++this._count;
        }
    }
}

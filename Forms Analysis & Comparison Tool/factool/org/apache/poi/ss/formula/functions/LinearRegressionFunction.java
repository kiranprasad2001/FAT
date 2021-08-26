// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.eval.RefEval;
import org.apache.poi.ss.formula.TwoDEval;
import org.apache.poi.ss.formula.eval.NumberEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.ValueEval;

public final class LinearRegressionFunction extends Fixed2ArgFunction
{
    public FUNCTION function;
    
    public LinearRegressionFunction(final FUNCTION function) {
        this.function = function;
    }
    
    @Override
    public ValueEval evaluate(final int srcRowIndex, final int srcColumnIndex, final ValueEval arg0, final ValueEval arg1) {
        double result;
        try {
            final LookupUtils.ValueVector vvY = createValueVector(arg0);
            final LookupUtils.ValueVector vvX = createValueVector(arg1);
            final int size = vvX.getSize();
            if (size == 0 || vvY.getSize() != size) {
                return ErrorEval.NA;
            }
            result = this.evaluateInternal(vvX, vvY, size);
        }
        catch (EvaluationException e) {
            return e.getErrorEval();
        }
        if (Double.isNaN(result) || Double.isInfinite(result)) {
            return ErrorEval.NUM_ERROR;
        }
        return new NumberEval(result);
    }
    
    private double evaluateInternal(final LookupUtils.ValueVector x, final LookupUtils.ValueVector y, final int size) throws EvaluationException {
        ErrorEval firstXerr = null;
        ErrorEval firstYerr = null;
        boolean accumlatedSome = false;
        double sumx = 0.0;
        double sumy = 0.0;
        for (int i = 0; i < size; ++i) {
            final ValueEval vx = x.getItem(i);
            final ValueEval vy = y.getItem(i);
            if (vx instanceof ErrorEval && firstXerr == null) {
                firstXerr = (ErrorEval)vx;
            }
            else if (vy instanceof ErrorEval && firstYerr == null) {
                firstYerr = (ErrorEval)vy;
            }
            else if (vx instanceof NumberEval && vy instanceof NumberEval) {
                accumlatedSome = true;
                final NumberEval nx = (NumberEval)vx;
                final NumberEval ny = (NumberEval)vy;
                sumx += nx.getNumberValue();
                sumy += ny.getNumberValue();
            }
        }
        final double xbar = sumx / size;
        final double ybar = sumy / size;
        double xxbar = 0.0;
        double xybar = 0.0;
        for (int j = 0; j < size; ++j) {
            final ValueEval vx2 = x.getItem(j);
            final ValueEval vy2 = y.getItem(j);
            if (vx2 instanceof ErrorEval && firstXerr == null) {
                firstXerr = (ErrorEval)vx2;
            }
            else if (vy2 instanceof ErrorEval && firstYerr == null) {
                firstYerr = (ErrorEval)vy2;
            }
            else if (vx2 instanceof NumberEval && vy2 instanceof NumberEval) {
                final NumberEval nx2 = (NumberEval)vx2;
                final NumberEval ny2 = (NumberEval)vy2;
                xxbar += (nx2.getNumberValue() - xbar) * (nx2.getNumberValue() - xbar);
                xybar += (nx2.getNumberValue() - xbar) * (ny2.getNumberValue() - ybar);
            }
        }
        final double beta1 = xybar / xxbar;
        final double beta2 = ybar - beta1 * xbar;
        if (firstXerr != null) {
            throw new EvaluationException(firstXerr);
        }
        if (firstYerr != null) {
            throw new EvaluationException(firstYerr);
        }
        if (!accumlatedSome) {
            throw new EvaluationException(ErrorEval.DIV_ZERO);
        }
        if (this.function == FUNCTION.INTERCEPT) {
            return beta2;
        }
        return beta1;
    }
    
    private static LookupUtils.ValueVector createValueVector(final ValueEval arg) throws EvaluationException {
        if (arg instanceof ErrorEval) {
            throw new EvaluationException((ErrorEval)arg);
        }
        if (arg instanceof TwoDEval) {
            return new AreaValueArray((TwoDEval)arg);
        }
        if (arg instanceof RefEval) {
            return new RefValueArray((RefEval)arg);
        }
        return new SingleCellValueArray(arg);
    }
    
    private abstract static class ValueArray implements LookupUtils.ValueVector
    {
        private final int _size;
        
        protected ValueArray(final int size) {
            this._size = size;
        }
        
        @Override
        public ValueEval getItem(final int index) {
            if (index < 0 || index > this._size) {
                throw new IllegalArgumentException("Specified index " + index + " is outside range (0.." + (this._size - 1) + ")");
            }
            return this.getItemInternal(index);
        }
        
        protected abstract ValueEval getItemInternal(final int p0);
        
        @Override
        public final int getSize() {
            return this._size;
        }
    }
    
    private static final class SingleCellValueArray extends ValueArray
    {
        private final ValueEval _value;
        
        public SingleCellValueArray(final ValueEval value) {
            super(1);
            this._value = value;
        }
        
        @Override
        protected ValueEval getItemInternal(final int index) {
            return this._value;
        }
    }
    
    private static final class RefValueArray extends ValueArray
    {
        private final RefEval _ref;
        private final int _width;
        
        public RefValueArray(final RefEval ref) {
            super(ref.getNumberOfSheets());
            this._ref = ref;
            this._width = ref.getNumberOfSheets();
        }
        
        @Override
        protected ValueEval getItemInternal(final int index) {
            final int sIx = index % this._width + this._ref.getFirstSheetIndex();
            return this._ref.getInnerValueEval(sIx);
        }
    }
    
    private static final class AreaValueArray extends ValueArray
    {
        private final TwoDEval _ae;
        private final int _width;
        
        public AreaValueArray(final TwoDEval ae) {
            super(ae.getWidth() * ae.getHeight());
            this._ae = ae;
            this._width = ae.getWidth();
        }
        
        @Override
        protected ValueEval getItemInternal(final int index) {
            final int rowIx = index / this._width;
            final int colIx = index % this._width;
            return this._ae.getValue(rowIx, colIx);
        }
    }
    
    public enum FUNCTION
    {
        INTERCEPT, 
        SLOPE;
    }
}

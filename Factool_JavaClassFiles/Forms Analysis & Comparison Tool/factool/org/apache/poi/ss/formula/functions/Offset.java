// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.eval.OperandResolver;
import org.apache.poi.ss.formula.eval.RefEval;
import org.apache.poi.ss.formula.eval.AreaEval;
import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.ValueEval;

public final class Offset implements Function
{
    private static final int LAST_VALID_ROW_INDEX = 65535;
    private static final int LAST_VALID_COLUMN_INDEX = 255;
    
    @Override
    public ValueEval evaluate(final ValueEval[] args, final int srcCellRow, final int srcCellCol) {
        if (args.length < 3 || args.length > 5) {
            return ErrorEval.VALUE_INVALID;
        }
        try {
            final BaseRef baseRef = evaluateBaseRef(args[0]);
            final int rowOffset = evaluateIntArg(args[1], srcCellRow, srcCellCol);
            final int columnOffset = evaluateIntArg(args[2], srcCellRow, srcCellCol);
            int height = baseRef.getHeight();
            int width = baseRef.getWidth();
            switch (args.length) {
                case 5: {
                    width = evaluateIntArg(args[4], srcCellRow, srcCellCol);
                }
                case 4: {
                    height = evaluateIntArg(args[3], srcCellRow, srcCellCol);
                    break;
                }
            }
            if (height == 0 || width == 0) {
                return ErrorEval.REF_INVALID;
            }
            final LinearOffsetRange rowOffsetRange = new LinearOffsetRange(rowOffset, height);
            final LinearOffsetRange colOffsetRange = new LinearOffsetRange(columnOffset, width);
            return createOffset(baseRef, rowOffsetRange, colOffsetRange);
        }
        catch (EvaluationException e) {
            return e.getErrorEval();
        }
    }
    
    private static AreaEval createOffset(final BaseRef baseRef, final LinearOffsetRange orRow, final LinearOffsetRange orCol) throws EvaluationException {
        final LinearOffsetRange absRows = orRow.normaliseAndTranslate(baseRef.getFirstRowIndex());
        final LinearOffsetRange absCols = orCol.normaliseAndTranslate(baseRef.getFirstColumnIndex());
        if (absRows.isOutOfBounds(0, 65535)) {
            throw new EvaluationException(ErrorEval.REF_INVALID);
        }
        if (absCols.isOutOfBounds(0, 255)) {
            throw new EvaluationException(ErrorEval.REF_INVALID);
        }
        return baseRef.offset(orRow.getFirstIndex(), orRow.getLastIndex(), orCol.getFirstIndex(), orCol.getLastIndex());
    }
    
    private static BaseRef evaluateBaseRef(final ValueEval eval) throws EvaluationException {
        if (eval instanceof RefEval) {
            return new BaseRef((RefEval)eval);
        }
        if (eval instanceof AreaEval) {
            return new BaseRef((AreaEval)eval);
        }
        if (eval instanceof ErrorEval) {
            throw new EvaluationException((ErrorEval)eval);
        }
        throw new EvaluationException(ErrorEval.VALUE_INVALID);
    }
    
    static int evaluateIntArg(final ValueEval eval, final int srcCellRow, final int srcCellCol) throws EvaluationException {
        final ValueEval ve = OperandResolver.getSingleValue(eval, srcCellRow, srcCellCol);
        return OperandResolver.coerceValueToInt(ve);
    }
    
    static final class LinearOffsetRange
    {
        private final int _offset;
        private final int _length;
        
        public LinearOffsetRange(final int offset, final int length) {
            if (length == 0) {
                throw new RuntimeException("length may not be zero");
            }
            this._offset = offset;
            this._length = length;
        }
        
        public short getFirstIndex() {
            return (short)this._offset;
        }
        
        public short getLastIndex() {
            return (short)(this._offset + this._length - 1);
        }
        
        public LinearOffsetRange normaliseAndTranslate(final int translationAmount) {
            if (this._length <= 0) {
                return new LinearOffsetRange(translationAmount + this._offset + this._length + 1, -this._length);
            }
            if (translationAmount == 0) {
                return this;
            }
            return new LinearOffsetRange(translationAmount + this._offset, this._length);
        }
        
        public boolean isOutOfBounds(final int lowValidIx, final int highValidIx) {
            return this._offset < lowValidIx || this.getLastIndex() > highValidIx;
        }
        
        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer(64);
            sb.append(this.getClass().getName()).append(" [");
            sb.append(this._offset).append("...").append(this.getLastIndex());
            sb.append("]");
            return sb.toString();
        }
    }
    
    private static final class BaseRef
    {
        private final int _firstRowIndex;
        private final int _firstColumnIndex;
        private final int _width;
        private final int _height;
        private final RefEval _refEval;
        private final AreaEval _areaEval;
        
        public BaseRef(final RefEval re) {
            this._refEval = re;
            this._areaEval = null;
            this._firstRowIndex = re.getRow();
            this._firstColumnIndex = re.getColumn();
            this._height = 1;
            this._width = 1;
        }
        
        public BaseRef(final AreaEval ae) {
            this._refEval = null;
            this._areaEval = ae;
            this._firstRowIndex = ae.getFirstRow();
            this._firstColumnIndex = ae.getFirstColumn();
            this._height = ae.getLastRow() - ae.getFirstRow() + 1;
            this._width = ae.getLastColumn() - ae.getFirstColumn() + 1;
        }
        
        public int getWidth() {
            return this._width;
        }
        
        public int getHeight() {
            return this._height;
        }
        
        public int getFirstRowIndex() {
            return this._firstRowIndex;
        }
        
        public int getFirstColumnIndex() {
            return this._firstColumnIndex;
        }
        
        public AreaEval offset(final int relFirstRowIx, final int relLastRowIx, final int relFirstColIx, final int relLastColIx) {
            if (this._refEval == null) {
                return this._areaEval.offset(relFirstRowIx, relLastRowIx, relFirstColIx, relLastColIx);
            }
            return this._refEval.offset(relFirstRowIx, relLastRowIx, relFirstColIx, relLastColIx);
        }
    }
}

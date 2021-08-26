// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula;

import org.apache.poi.ss.formula.eval.ValueEval;

final class SheetRangeEvaluator implements SheetRange
{
    private final int _firstSheetIndex;
    private final int _lastSheetIndex;
    private SheetRefEvaluator[] _sheetEvaluators;
    
    public SheetRangeEvaluator(final int firstSheetIndex, final int lastSheetIndex, final SheetRefEvaluator[] sheetEvaluators) {
        if (firstSheetIndex < 0) {
            throw new IllegalArgumentException("Invalid firstSheetIndex: " + firstSheetIndex + ".");
        }
        if (lastSheetIndex < firstSheetIndex) {
            throw new IllegalArgumentException("Invalid lastSheetIndex: " + lastSheetIndex + " for firstSheetIndex: " + firstSheetIndex + ".");
        }
        this._firstSheetIndex = firstSheetIndex;
        this._lastSheetIndex = lastSheetIndex;
        this._sheetEvaluators = sheetEvaluators;
    }
    
    public SheetRangeEvaluator(final int onlySheetIndex, final SheetRefEvaluator sheetEvaluator) {
        this(onlySheetIndex, onlySheetIndex, new SheetRefEvaluator[] { sheetEvaluator });
    }
    
    public SheetRefEvaluator getSheetEvaluator(final int sheetIndex) {
        if (sheetIndex < this._firstSheetIndex || sheetIndex > this._lastSheetIndex) {
            throw new IllegalArgumentException("Invalid SheetIndex: " + sheetIndex + " - Outside range " + this._firstSheetIndex + " : " + this._lastSheetIndex);
        }
        return this._sheetEvaluators[sheetIndex - this._firstSheetIndex];
    }
    
    @Override
    public int getFirstSheetIndex() {
        return this._firstSheetIndex;
    }
    
    @Override
    public int getLastSheetIndex() {
        return this._lastSheetIndex;
    }
    
    public String getSheetName(final int sheetIndex) {
        return this.getSheetEvaluator(sheetIndex).getSheetName();
    }
    
    public String getSheetNameRange() {
        final StringBuilder sb = new StringBuilder();
        sb.append(this.getSheetName(this._firstSheetIndex));
        if (this._firstSheetIndex != this._lastSheetIndex) {
            sb.append(':');
            sb.append(this.getSheetName(this._lastSheetIndex));
        }
        return sb.toString();
    }
    
    public ValueEval getEvalForCell(final int sheetIndex, final int rowIndex, final int columnIndex) {
        return this.getSheetEvaluator(sheetIndex).getEvalForCell(rowIndex, columnIndex);
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula;

import org.apache.poi.ss.util.CellReference;
import org.apache.poi.ss.formula.eval.AreaEval;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.ptg.AreaI;
import org.apache.poi.ss.formula.eval.AreaEvalBase;

final class LazyAreaEval extends AreaEvalBase
{
    private final SheetRangeEvaluator _evaluator;
    
    LazyAreaEval(final AreaI ptg, final SheetRangeEvaluator evaluator) {
        super(ptg, evaluator);
        this._evaluator = evaluator;
    }
    
    public LazyAreaEval(final int firstRowIndex, final int firstColumnIndex, final int lastRowIndex, final int lastColumnIndex, final SheetRangeEvaluator evaluator) {
        super(evaluator, firstRowIndex, firstColumnIndex, lastRowIndex, lastColumnIndex);
        this._evaluator = evaluator;
    }
    
    @Override
    public ValueEval getRelativeValue(final int relativeRowIndex, final int relativeColumnIndex) {
        return this.getRelativeValue(this.getFirstSheetIndex(), relativeRowIndex, relativeColumnIndex);
    }
    
    @Override
    public ValueEval getRelativeValue(final int sheetIndex, final int relativeRowIndex, final int relativeColumnIndex) {
        final int rowIx = relativeRowIndex + this.getFirstRow();
        final int colIx = relativeColumnIndex + this.getFirstColumn();
        return this._evaluator.getEvalForCell(sheetIndex, rowIx, colIx);
    }
    
    @Override
    public AreaEval offset(final int relFirstRowIx, final int relLastRowIx, final int relFirstColIx, final int relLastColIx) {
        final AreaI area = new AreaI.OffsetArea(this.getFirstRow(), this.getFirstColumn(), relFirstRowIx, relLastRowIx, relFirstColIx, relLastColIx);
        return new LazyAreaEval(area, this._evaluator);
    }
    
    @Override
    public LazyAreaEval getRow(final int rowIndex) {
        if (rowIndex >= this.getHeight()) {
            throw new IllegalArgumentException("Invalid rowIndex " + rowIndex + ".  Allowable range is (0.." + this.getHeight() + ").");
        }
        final int absRowIx = this.getFirstRow() + rowIndex;
        return new LazyAreaEval(absRowIx, this.getFirstColumn(), absRowIx, this.getLastColumn(), this._evaluator);
    }
    
    @Override
    public LazyAreaEval getColumn(final int columnIndex) {
        if (columnIndex >= this.getWidth()) {
            throw new IllegalArgumentException("Invalid columnIndex " + columnIndex + ".  Allowable range is (0.." + this.getWidth() + ").");
        }
        final int absColIx = this.getFirstColumn() + columnIndex;
        return new LazyAreaEval(this.getFirstRow(), absColIx, this.getLastRow(), absColIx, this._evaluator);
    }
    
    @Override
    public String toString() {
        final CellReference crA = new CellReference(this.getFirstRow(), this.getFirstColumn());
        final CellReference crB = new CellReference(this.getLastRow(), this.getLastColumn());
        final StringBuffer sb = new StringBuffer();
        sb.append(this.getClass().getName()).append("[");
        sb.append(this._evaluator.getSheetNameRange());
        sb.append('!');
        sb.append(crA.formatAsString());
        sb.append(':');
        sb.append(crB.formatAsString());
        sb.append("]");
        return sb.toString();
    }
    
    @Override
    public boolean isSubTotal(final int rowIndex, final int columnIndex) {
        final SheetRefEvaluator _sre = this._evaluator.getSheetEvaluator(this._evaluator.getFirstSheetIndex());
        return _sre.isSubTotal(this.getFirstRow() + rowIndex, this.getFirstColumn() + columnIndex);
    }
}

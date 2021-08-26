// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula;

import org.apache.poi.ss.util.CellReference;
import org.apache.poi.ss.formula.ptg.AreaI;
import org.apache.poi.ss.formula.eval.AreaEval;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.eval.RefEvalBase;

final class LazyRefEval extends RefEvalBase
{
    private final SheetRangeEvaluator _evaluator;
    
    public LazyRefEval(final int rowIndex, final int columnIndex, final SheetRangeEvaluator sre) {
        super(sre, rowIndex, columnIndex);
        this._evaluator = sre;
    }
    
    @Deprecated
    public ValueEval getInnerValueEval() {
        return this.getInnerValueEval(this._evaluator.getFirstSheetIndex());
    }
    
    @Override
    public ValueEval getInnerValueEval(final int sheetIndex) {
        return this._evaluator.getEvalForCell(sheetIndex, this.getRow(), this.getColumn());
    }
    
    @Override
    public AreaEval offset(final int relFirstRowIx, final int relLastRowIx, final int relFirstColIx, final int relLastColIx) {
        final AreaI area = new AreaI.OffsetArea(this.getRow(), this.getColumn(), relFirstRowIx, relLastRowIx, relFirstColIx, relLastColIx);
        return new LazyAreaEval(area, this._evaluator);
    }
    
    @Override
    public String toString() {
        final CellReference cr = new CellReference(this.getRow(), this.getColumn());
        final StringBuffer sb = new StringBuffer();
        sb.append(this.getClass().getName()).append("[");
        sb.append(this._evaluator.getSheetNameRange());
        sb.append('!');
        sb.append(cr.formatAsString());
        sb.append("]");
        return sb.toString();
    }
}

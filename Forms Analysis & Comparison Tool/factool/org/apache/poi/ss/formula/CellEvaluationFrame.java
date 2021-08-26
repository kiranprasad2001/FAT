// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula;

import org.apache.poi.ss.formula.eval.ValueEval;
import java.util.HashSet;
import java.util.Set;

final class CellEvaluationFrame
{
    private final FormulaCellCacheEntry _cce;
    private final Set<CellCacheEntry> _sensitiveInputCells;
    private FormulaUsedBlankCellSet _usedBlankCellGroup;
    
    public CellEvaluationFrame(final FormulaCellCacheEntry cce) {
        this._cce = cce;
        this._sensitiveInputCells = new HashSet<CellCacheEntry>();
    }
    
    public CellCacheEntry getCCE() {
        return this._cce;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer(64);
        sb.append(this.getClass().getName()).append(" [");
        sb.append("]");
        return sb.toString();
    }
    
    public void addSensitiveInputCell(final CellCacheEntry inputCell) {
        this._sensitiveInputCells.add(inputCell);
    }
    
    private CellCacheEntry[] getSensitiveInputCells() {
        final int nItems = this._sensitiveInputCells.size();
        if (nItems < 1) {
            return CellCacheEntry.EMPTY_ARRAY;
        }
        final CellCacheEntry[] result = new CellCacheEntry[nItems];
        this._sensitiveInputCells.toArray(result);
        return result;
    }
    
    public void addUsedBlankCell(final int bookIndex, final int sheetIndex, final int rowIndex, final int columnIndex) {
        if (this._usedBlankCellGroup == null) {
            this._usedBlankCellGroup = new FormulaUsedBlankCellSet();
        }
        this._usedBlankCellGroup.addCell(bookIndex, sheetIndex, rowIndex, columnIndex);
    }
    
    public void updateFormulaResult(final ValueEval result) {
        this._cce.updateFormulaResult(result, this.getSensitiveInputCells(), this._usedBlankCellGroup);
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.eval.forked;

import org.apache.poi.ss.formula.EvaluationWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import java.util.Arrays;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.ss.formula.EvaluationCell;
import java.util.HashMap;
import java.util.Map;
import org.apache.poi.ss.formula.EvaluationSheet;

final class ForkedEvaluationSheet implements EvaluationSheet
{
    private final EvaluationSheet _masterSheet;
    private final Map<RowColKey, ForkedEvaluationCell> _sharedCellsByRowCol;
    
    public ForkedEvaluationSheet(final EvaluationSheet masterSheet) {
        this._masterSheet = masterSheet;
        this._sharedCellsByRowCol = new HashMap<RowColKey, ForkedEvaluationCell>();
    }
    
    @Override
    public EvaluationCell getCell(final int rowIndex, final int columnIndex) {
        final RowColKey key = new RowColKey(rowIndex, columnIndex);
        final ForkedEvaluationCell result = this._sharedCellsByRowCol.get(key);
        if (result == null) {
            return this._masterSheet.getCell(rowIndex, columnIndex);
        }
        return result;
    }
    
    public ForkedEvaluationCell getOrCreateUpdatableCell(final int rowIndex, final int columnIndex) {
        final RowColKey key = new RowColKey(rowIndex, columnIndex);
        ForkedEvaluationCell result = this._sharedCellsByRowCol.get(key);
        if (result == null) {
            final EvaluationCell mcell = this._masterSheet.getCell(rowIndex, columnIndex);
            if (mcell == null) {
                final CellReference cr = new CellReference(rowIndex, columnIndex);
                throw new UnsupportedOperationException("Underlying cell '" + cr.formatAsString() + "' is missing in master sheet.");
            }
            result = new ForkedEvaluationCell(this, mcell);
            this._sharedCellsByRowCol.put(key, result);
        }
        return result;
    }
    
    public void copyUpdatedCells(final Sheet sheet) {
        final RowColKey[] keys = new RowColKey[this._sharedCellsByRowCol.size()];
        this._sharedCellsByRowCol.keySet().toArray(keys);
        Arrays.sort(keys);
        for (int i = 0; i < keys.length; ++i) {
            final RowColKey key = keys[i];
            Row row = sheet.getRow(key.getRowIndex());
            if (row == null) {
                row = sheet.createRow(key.getRowIndex());
            }
            Cell destCell = row.getCell(key.getColumnIndex());
            if (destCell == null) {
                destCell = row.createCell(key.getColumnIndex());
            }
            final ForkedEvaluationCell srcCell = this._sharedCellsByRowCol.get(key);
            srcCell.copyValue(destCell);
        }
    }
    
    public int getSheetIndex(final EvaluationWorkbook mewb) {
        return mewb.getSheetIndex(this._masterSheet);
    }
    
    private static final class RowColKey implements Comparable<RowColKey>
    {
        private final int _rowIndex;
        private final int _columnIndex;
        
        public RowColKey(final int rowIndex, final int columnIndex) {
            this._rowIndex = rowIndex;
            this._columnIndex = columnIndex;
        }
        
        @Override
        public boolean equals(final Object obj) {
            assert obj instanceof RowColKey : "these private cache key instances are only compared to themselves";
            final RowColKey other = (RowColKey)obj;
            return this._rowIndex == other._rowIndex && this._columnIndex == other._columnIndex;
        }
        
        @Override
        public int hashCode() {
            return this._rowIndex ^ this._columnIndex;
        }
        
        @Override
        public int compareTo(final RowColKey o) {
            final int cmp = this._rowIndex - o._rowIndex;
            if (cmp != 0) {
                return cmp;
            }
            return this._columnIndex - o._columnIndex;
        }
        
        public int getRowIndex() {
            return this._rowIndex;
        }
        
        public int getColumnIndex() {
            return this._columnIndex;
        }
    }
}

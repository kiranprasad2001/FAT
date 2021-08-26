// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.hssf.usermodel;

import org.apache.poi.ss.formula.EvaluationCell;
import org.apache.poi.ss.formula.EvaluationSheet;

final class HSSFEvaluationSheet implements EvaluationSheet
{
    private final HSSFSheet _hs;
    
    public HSSFEvaluationSheet(final HSSFSheet hs) {
        this._hs = hs;
    }
    
    public HSSFSheet getHSSFSheet() {
        return this._hs;
    }
    
    @Override
    public EvaluationCell getCell(final int rowIndex, final int columnIndex) {
        final HSSFRow row = this._hs.getRow(rowIndex);
        if (row == null) {
            return null;
        }
        final HSSFCell cell = row.getCell(columnIndex);
        if (cell == null) {
            return null;
        }
        return new HSSFEvaluationCell(cell, this);
    }
}

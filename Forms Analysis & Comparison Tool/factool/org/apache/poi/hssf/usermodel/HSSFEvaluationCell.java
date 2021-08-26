// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.hssf.usermodel;

import org.apache.poi.ss.formula.EvaluationSheet;
import org.apache.poi.ss.formula.EvaluationCell;

final class HSSFEvaluationCell implements EvaluationCell
{
    private final EvaluationSheet _evalSheet;
    private final HSSFCell _cell;
    
    public HSSFEvaluationCell(final HSSFCell cell, final EvaluationSheet evalSheet) {
        this._cell = cell;
        this._evalSheet = evalSheet;
    }
    
    public HSSFEvaluationCell(final HSSFCell cell) {
        this(cell, new HSSFEvaluationSheet(cell.getSheet()));
    }
    
    @Override
    public Object getIdentityKey() {
        return this._cell;
    }
    
    public HSSFCell getHSSFCell() {
        return this._cell;
    }
    
    @Override
    public boolean getBooleanCellValue() {
        return this._cell.getBooleanCellValue();
    }
    
    @Override
    public int getCellType() {
        return this._cell.getCellType();
    }
    
    @Override
    public int getColumnIndex() {
        return this._cell.getColumnIndex();
    }
    
    @Override
    public int getErrorCellValue() {
        return this._cell.getErrorCellValue();
    }
    
    @Override
    public double getNumericCellValue() {
        return this._cell.getNumericCellValue();
    }
    
    @Override
    public int getRowIndex() {
        return this._cell.getRowIndex();
    }
    
    @Override
    public EvaluationSheet getSheet() {
        return this._evalSheet;
    }
    
    @Override
    public String getStringCellValue() {
        return this._cell.getRichStringCellValue().getString();
    }
    
    @Override
    public int getCachedFormulaResultType() {
        return this._cell.getCachedFormulaResultType();
    }
}

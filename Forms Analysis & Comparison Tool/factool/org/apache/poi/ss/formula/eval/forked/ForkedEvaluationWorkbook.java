// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.eval.forked;

import org.apache.poi.ss.formula.udf.UDFFinder;
import org.apache.poi.ss.formula.ptg.NameXPtg;
import org.apache.poi.ss.formula.EvaluationSheet;
import org.apache.poi.ss.formula.EvaluationName;
import org.apache.poi.ss.formula.ptg.NamePtg;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.formula.EvaluationCell;
import java.util.HashMap;
import java.util.Map;
import org.apache.poi.ss.formula.EvaluationWorkbook;

final class ForkedEvaluationWorkbook implements EvaluationWorkbook
{
    private final EvaluationWorkbook _masterBook;
    private final Map<String, ForkedEvaluationSheet> _sharedSheetsByName;
    
    public ForkedEvaluationWorkbook(final EvaluationWorkbook master) {
        this._masterBook = master;
        this._sharedSheetsByName = new HashMap<String, ForkedEvaluationSheet>();
    }
    
    public ForkedEvaluationCell getOrCreateUpdatableCell(final String sheetName, final int rowIndex, final int columnIndex) {
        final ForkedEvaluationSheet sheet = this.getSharedSheet(sheetName);
        return sheet.getOrCreateUpdatableCell(rowIndex, columnIndex);
    }
    
    public EvaluationCell getEvaluationCell(final String sheetName, final int rowIndex, final int columnIndex) {
        final ForkedEvaluationSheet sheet = this.getSharedSheet(sheetName);
        return sheet.getCell(rowIndex, columnIndex);
    }
    
    private ForkedEvaluationSheet getSharedSheet(final String sheetName) {
        ForkedEvaluationSheet result = this._sharedSheetsByName.get(sheetName);
        if (result == null) {
            result = new ForkedEvaluationSheet(this._masterBook.getSheet(this._masterBook.getSheetIndex(sheetName)));
            this._sharedSheetsByName.put(sheetName, result);
        }
        return result;
    }
    
    public void copyUpdatedCells(final Workbook workbook) {
        final String[] sheetNames = new String[this._sharedSheetsByName.size()];
        this._sharedSheetsByName.keySet().toArray(sheetNames);
        final OrderedSheet[] oss = new OrderedSheet[sheetNames.length];
        for (int i = 0; i < sheetNames.length; ++i) {
            final String sheetName = sheetNames[i];
            oss[i] = new OrderedSheet(sheetName, this._masterBook.getSheetIndex(sheetName));
        }
        for (int i = 0; i < oss.length; ++i) {
            final String sheetName = oss[i].getSheetName();
            final ForkedEvaluationSheet sheet = this._sharedSheetsByName.get(sheetName);
            sheet.copyUpdatedCells(workbook.getSheet(sheetName));
        }
    }
    
    @Override
    public int convertFromExternSheetIndex(final int externSheetIndex) {
        return this._masterBook.convertFromExternSheetIndex(externSheetIndex);
    }
    
    @Override
    public ExternalSheet getExternalSheet(final int externSheetIndex) {
        return this._masterBook.getExternalSheet(externSheetIndex);
    }
    
    @Override
    public ExternalSheet getExternalSheet(final String firstSheetName, final String lastSheetName, final int externalWorkbookNumber) {
        return this._masterBook.getExternalSheet(firstSheetName, lastSheetName, externalWorkbookNumber);
    }
    
    @Override
    public Ptg[] getFormulaTokens(final EvaluationCell cell) {
        if (cell instanceof ForkedEvaluationCell) {
            throw new RuntimeException("Updated formulas not supported yet");
        }
        return this._masterBook.getFormulaTokens(cell);
    }
    
    @Override
    public EvaluationName getName(final NamePtg namePtg) {
        return this._masterBook.getName(namePtg);
    }
    
    @Override
    public EvaluationName getName(final String name, final int sheetIndex) {
        return this._masterBook.getName(name, sheetIndex);
    }
    
    @Override
    public EvaluationSheet getSheet(final int sheetIndex) {
        return this.getSharedSheet(this.getSheetName(sheetIndex));
    }
    
    @Override
    public ExternalName getExternalName(final int externSheetIndex, final int externNameIndex) {
        return this._masterBook.getExternalName(externSheetIndex, externNameIndex);
    }
    
    @Override
    public ExternalName getExternalName(final String nameName, final String sheetName, final int externalWorkbookNumber) {
        return this._masterBook.getExternalName(nameName, sheetName, externalWorkbookNumber);
    }
    
    @Override
    public int getSheetIndex(final EvaluationSheet sheet) {
        if (sheet instanceof ForkedEvaluationSheet) {
            final ForkedEvaluationSheet mes = (ForkedEvaluationSheet)sheet;
            return mes.getSheetIndex(this._masterBook);
        }
        return this._masterBook.getSheetIndex(sheet);
    }
    
    @Override
    public int getSheetIndex(final String sheetName) {
        return this._masterBook.getSheetIndex(sheetName);
    }
    
    @Override
    public String getSheetName(final int sheetIndex) {
        return this._masterBook.getSheetName(sheetIndex);
    }
    
    @Override
    public String resolveNameXText(final NameXPtg ptg) {
        return this._masterBook.resolveNameXText(ptg);
    }
    
    @Override
    public UDFFinder getUDFFinder() {
        return this._masterBook.getUDFFinder();
    }
    
    private static final class OrderedSheet implements Comparable<OrderedSheet>
    {
        private final String _sheetName;
        private final int _index;
        
        public OrderedSheet(final String sheetName, final int index) {
            this._sheetName = sheetName;
            this._index = index;
        }
        
        public String getSheetName() {
            return this._sheetName;
        }
        
        @Override
        public int compareTo(final OrderedSheet o) {
            return this._index - o._index;
        }
    }
}

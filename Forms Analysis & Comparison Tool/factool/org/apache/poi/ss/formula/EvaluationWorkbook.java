// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula;

import org.apache.poi.ss.formula.udf.UDFFinder;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.ss.formula.ptg.NameXPtg;
import org.apache.poi.ss.formula.ptg.NamePtg;

public interface EvaluationWorkbook
{
    String getSheetName(final int p0);
    
    int getSheetIndex(final EvaluationSheet p0);
    
    int getSheetIndex(final String p0);
    
    EvaluationSheet getSheet(final int p0);
    
    ExternalSheet getExternalSheet(final int p0);
    
    ExternalSheet getExternalSheet(final String p0, final String p1, final int p2);
    
    int convertFromExternSheetIndex(final int p0);
    
    ExternalName getExternalName(final int p0, final int p1);
    
    ExternalName getExternalName(final String p0, final String p1, final int p2);
    
    EvaluationName getName(final NamePtg p0);
    
    EvaluationName getName(final String p0, final int p1);
    
    String resolveNameXText(final NameXPtg p0);
    
    Ptg[] getFormulaTokens(final EvaluationCell p0);
    
    UDFFinder getUDFFinder();
    
    public static class ExternalSheet
    {
        private final String _workbookName;
        private final String _sheetName;
        
        public ExternalSheet(final String workbookName, final String sheetName) {
            this._workbookName = workbookName;
            this._sheetName = sheetName;
        }
        
        public String getWorkbookName() {
            return this._workbookName;
        }
        
        public String getSheetName() {
            return this._sheetName;
        }
    }
    
    public static class ExternalSheetRange extends ExternalSheet
    {
        private final String _lastSheetName;
        
        public ExternalSheetRange(final String workbookName, final String firstSheetName, final String lastSheetName) {
            super(workbookName, firstSheetName);
            this._lastSheetName = lastSheetName;
        }
        
        public String getFirstSheetName() {
            return this.getSheetName();
        }
        
        public String getLastSheetName() {
            return this._lastSheetName;
        }
    }
    
    public static class ExternalName
    {
        private final String _nameName;
        private final int _nameNumber;
        private final int _ix;
        
        public ExternalName(final String nameName, final int nameNumber, final int ix) {
            this._nameName = nameName;
            this._nameNumber = nameNumber;
            this._ix = ix;
        }
        
        public String getName() {
            return this._nameName;
        }
        
        public int getNumber() {
            return this._nameNumber;
        }
        
        public int getIx() {
            return this._ix;
        }
    }
}

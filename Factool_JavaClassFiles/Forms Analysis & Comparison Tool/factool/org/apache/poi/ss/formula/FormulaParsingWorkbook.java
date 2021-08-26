// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula;

import org.apache.poi.ss.SpreadsheetVersion;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.ss.formula.ptg.Ptg;

public interface FormulaParsingWorkbook
{
    EvaluationName getName(final String p0, final int p1);
    
    Ptg getNameXPtg(final String p0, final SheetIdentifier p1);
    
    Ptg get3DReferencePtg(final CellReference p0, final SheetIdentifier p1);
    
    Ptg get3DReferencePtg(final AreaReference p0, final SheetIdentifier p1);
    
    int getExternalSheetIndex(final String p0);
    
    int getExternalSheetIndex(final String p0, final String p1);
    
    SpreadsheetVersion getSpreadsheetVersion();
}

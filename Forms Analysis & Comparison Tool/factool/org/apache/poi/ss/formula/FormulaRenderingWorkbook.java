// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula;

import org.apache.poi.ss.formula.ptg.NamePtg;
import org.apache.poi.ss.formula.ptg.NameXPtg;

public interface FormulaRenderingWorkbook
{
    EvaluationWorkbook.ExternalSheet getExternalSheet(final int p0);
    
    String getSheetFirstNameByExternSheet(final int p0);
    
    String getSheetLastNameByExternSheet(final int p0);
    
    String resolveNameXText(final NameXPtg p0);
    
    String getNameText(final NamePtg p0);
}

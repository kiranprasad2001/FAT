// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.usermodel;

import java.util.Map;

public interface FormulaEvaluator
{
    void clearAllCachedResultValues();
    
    void notifySetFormula(final Cell p0);
    
    void notifyDeleteCell(final Cell p0);
    
    void notifyUpdateCell(final Cell p0);
    
    void evaluateAll();
    
    CellValue evaluate(final Cell p0);
    
    int evaluateFormulaCell(final Cell p0);
    
    Cell evaluateInCell(final Cell p0);
    
    void setupReferencedWorkbooks(final Map<String, FormulaEvaluator> p0);
    
    void setIgnoreMissingWorkbooks(final boolean p0);
    
    void setDebugEvaluationOutputForNextEval(final boolean p0);
}

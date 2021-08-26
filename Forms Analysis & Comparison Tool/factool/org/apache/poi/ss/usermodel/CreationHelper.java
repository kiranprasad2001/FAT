// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.usermodel;

public interface CreationHelper
{
    RichTextString createRichTextString(final String p0);
    
    DataFormat createDataFormat();
    
    Hyperlink createHyperlink(final int p0);
    
    FormulaEvaluator createFormulaEvaluator();
    
    ClientAnchor createClientAnchor();
}

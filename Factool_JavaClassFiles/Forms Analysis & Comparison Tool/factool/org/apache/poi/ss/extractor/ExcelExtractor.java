// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.extractor;

public interface ExcelExtractor
{
    void setIncludeSheetNames(final boolean p0);
    
    void setFormulasNotResults(final boolean p0);
    
    void setIncludeHeadersFooters(final boolean p0);
    
    void setIncludeCellComments(final boolean p0);
    
    String getText();
}

// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.usermodel;

import org.apache.poi.ss.util.CellRangeAddressList;

public interface DataValidation
{
    DataValidationConstraint getValidationConstraint();
    
    void setErrorStyle(final int p0);
    
    int getErrorStyle();
    
    void setEmptyCellAllowed(final boolean p0);
    
    boolean getEmptyCellAllowed();
    
    void setSuppressDropDownArrow(final boolean p0);
    
    boolean getSuppressDropDownArrow();
    
    void setShowPromptBox(final boolean p0);
    
    boolean getShowPromptBox();
    
    void setShowErrorBox(final boolean p0);
    
    boolean getShowErrorBox();
    
    void createPromptBox(final String p0, final String p1);
    
    String getPromptBoxTitle();
    
    String getPromptBoxText();
    
    void createErrorBox(final String p0, final String p1);
    
    String getErrorBoxTitle();
    
    String getErrorBoxText();
    
    CellRangeAddressList getRegions();
    
    public static final class ErrorStyle
    {
        public static final int STOP = 0;
        public static final int WARNING = 1;
        public static final int INFO = 2;
    }
}

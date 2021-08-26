// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.usermodel;

import org.apache.poi.ss.util.CellRangeAddress;

public interface ConditionalFormatting
{
    CellRangeAddress[] getFormattingRanges();
    
    void setRule(final int p0, final ConditionalFormattingRule p1);
    
    void addRule(final ConditionalFormattingRule p0);
    
    ConditionalFormattingRule getRule(final int p0);
    
    int getNumberOfRules();
}

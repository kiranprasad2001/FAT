// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.usermodel;

import org.apache.poi.ss.util.CellRangeAddress;

public interface SheetConditionalFormatting
{
    int addConditionalFormatting(final CellRangeAddress[] p0, final ConditionalFormattingRule p1);
    
    int addConditionalFormatting(final CellRangeAddress[] p0, final ConditionalFormattingRule p1, final ConditionalFormattingRule p2);
    
    int addConditionalFormatting(final CellRangeAddress[] p0, final ConditionalFormattingRule[] p1);
    
    int addConditionalFormatting(final ConditionalFormatting p0);
    
    ConditionalFormattingRule createConditionalFormattingRule(final byte p0, final String p1, final String p2);
    
    ConditionalFormattingRule createConditionalFormattingRule(final byte p0, final String p1);
    
    ConditionalFormattingRule createConditionalFormattingRule(final String p0);
    
    ConditionalFormatting getConditionalFormattingAt(final int p0);
    
    int getNumConditionalFormattings();
    
    void removeConditionalFormatting(final int p0);
}

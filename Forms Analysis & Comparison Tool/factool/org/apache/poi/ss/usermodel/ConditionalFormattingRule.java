// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.usermodel;

public interface ConditionalFormattingRule
{
    public static final byte CONDITION_TYPE_CELL_VALUE_IS = 1;
    public static final byte CONDITION_TYPE_FORMULA = 2;
    
    BorderFormatting createBorderFormatting();
    
    BorderFormatting getBorderFormatting();
    
    FontFormatting createFontFormatting();
    
    FontFormatting getFontFormatting();
    
    PatternFormatting createPatternFormatting();
    
    PatternFormatting getPatternFormatting();
    
    byte getConditionType();
    
    byte getComparisonOperation();
    
    String getFormula1();
    
    String getFormula2();
}

// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.hssf.usermodel;

import org.apache.poi.hssf.record.CFRuleRecord;
import org.apache.poi.ss.usermodel.PatternFormatting;

public class HSSFPatternFormatting implements PatternFormatting
{
    private final CFRuleRecord cfRuleRecord;
    private final org.apache.poi.hssf.record.cf.PatternFormatting patternFormatting;
    
    protected HSSFPatternFormatting(final CFRuleRecord cfRuleRecord) {
        this.cfRuleRecord = cfRuleRecord;
        this.patternFormatting = cfRuleRecord.getPatternFormatting();
    }
    
    protected org.apache.poi.hssf.record.cf.PatternFormatting getPatternFormattingBlock() {
        return this.patternFormatting;
    }
    
    @Override
    public short getFillBackgroundColor() {
        return (short)this.patternFormatting.getFillBackgroundColor();
    }
    
    @Override
    public short getFillForegroundColor() {
        return (short)this.patternFormatting.getFillForegroundColor();
    }
    
    @Override
    public short getFillPattern() {
        return (short)this.patternFormatting.getFillPattern();
    }
    
    @Override
    public void setFillBackgroundColor(final short bg) {
        this.patternFormatting.setFillBackgroundColor(bg);
        if (bg != 0) {
            this.cfRuleRecord.setPatternBackgroundColorModified(true);
        }
    }
    
    @Override
    public void setFillForegroundColor(final short fg) {
        this.patternFormatting.setFillForegroundColor(fg);
        if (fg != 0) {
            this.cfRuleRecord.setPatternColorModified(true);
        }
    }
    
    @Override
    public void setFillPattern(final short fp) {
        this.patternFormatting.setFillPattern(fp);
        if (fp != 0) {
            this.cfRuleRecord.setPatternStyleModified(true);
        }
    }
}

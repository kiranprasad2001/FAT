// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.hssf.usermodel;

import org.apache.poi.hssf.model.HSSFFormulaParser;
import org.apache.poi.ss.formula.ptg.Ptg;
import org.apache.poi.hssf.record.cf.PatternFormatting;
import org.apache.poi.hssf.record.cf.BorderFormatting;
import org.apache.poi.hssf.record.cf.FontFormatting;
import org.apache.poi.hssf.record.CFRuleRecord;
import org.apache.poi.ss.usermodel.ConditionalFormattingRule;

public final class HSSFConditionalFormattingRule implements ConditionalFormattingRule
{
    private static final byte CELL_COMPARISON = 1;
    private final CFRuleRecord cfRuleRecord;
    private final HSSFWorkbook workbook;
    
    HSSFConditionalFormattingRule(final HSSFWorkbook pWorkbook, final CFRuleRecord pRuleRecord) {
        if (pWorkbook == null) {
            throw new IllegalArgumentException("pWorkbook must not be null");
        }
        if (pRuleRecord == null) {
            throw new IllegalArgumentException("pRuleRecord must not be null");
        }
        this.workbook = pWorkbook;
        this.cfRuleRecord = pRuleRecord;
    }
    
    CFRuleRecord getCfRuleRecord() {
        return this.cfRuleRecord;
    }
    
    private HSSFFontFormatting getFontFormatting(final boolean create) {
        FontFormatting fontFormatting = this.cfRuleRecord.getFontFormatting();
        if (fontFormatting != null) {
            this.cfRuleRecord.setFontFormatting(fontFormatting);
            return new HSSFFontFormatting(this.cfRuleRecord);
        }
        if (create) {
            fontFormatting = new FontFormatting();
            this.cfRuleRecord.setFontFormatting(fontFormatting);
            return new HSSFFontFormatting(this.cfRuleRecord);
        }
        return null;
    }
    
    @Override
    public HSSFFontFormatting getFontFormatting() {
        return this.getFontFormatting(false);
    }
    
    @Override
    public HSSFFontFormatting createFontFormatting() {
        return this.getFontFormatting(true);
    }
    
    private HSSFBorderFormatting getBorderFormatting(final boolean create) {
        BorderFormatting borderFormatting = this.cfRuleRecord.getBorderFormatting();
        if (borderFormatting != null) {
            this.cfRuleRecord.setBorderFormatting(borderFormatting);
            return new HSSFBorderFormatting(this.cfRuleRecord);
        }
        if (create) {
            borderFormatting = new BorderFormatting();
            this.cfRuleRecord.setBorderFormatting(borderFormatting);
            return new HSSFBorderFormatting(this.cfRuleRecord);
        }
        return null;
    }
    
    @Override
    public HSSFBorderFormatting getBorderFormatting() {
        return this.getBorderFormatting(false);
    }
    
    @Override
    public HSSFBorderFormatting createBorderFormatting() {
        return this.getBorderFormatting(true);
    }
    
    private HSSFPatternFormatting getPatternFormatting(final boolean create) {
        PatternFormatting patternFormatting = this.cfRuleRecord.getPatternFormatting();
        if (patternFormatting != null) {
            this.cfRuleRecord.setPatternFormatting(patternFormatting);
            return new HSSFPatternFormatting(this.cfRuleRecord);
        }
        if (create) {
            patternFormatting = new PatternFormatting();
            this.cfRuleRecord.setPatternFormatting(patternFormatting);
            return new HSSFPatternFormatting(this.cfRuleRecord);
        }
        return null;
    }
    
    @Override
    public HSSFPatternFormatting getPatternFormatting() {
        return this.getPatternFormatting(false);
    }
    
    @Override
    public HSSFPatternFormatting createPatternFormatting() {
        return this.getPatternFormatting(true);
    }
    
    @Override
    public byte getConditionType() {
        return this.cfRuleRecord.getConditionType();
    }
    
    @Override
    public byte getComparisonOperation() {
        return this.cfRuleRecord.getComparisonOperation();
    }
    
    @Override
    public String getFormula1() {
        return this.toFormulaString(this.cfRuleRecord.getParsedExpression1());
    }
    
    @Override
    public String getFormula2() {
        final byte conditionType = this.cfRuleRecord.getConditionType();
        if (conditionType == 1) {
            final byte comparisonOperation = this.cfRuleRecord.getComparisonOperation();
            switch (comparisonOperation) {
                case 1:
                case 2: {
                    return this.toFormulaString(this.cfRuleRecord.getParsedExpression2());
                }
            }
        }
        return null;
    }
    
    private String toFormulaString(final Ptg[] parsedExpression) {
        if (parsedExpression == null) {
            return null;
        }
        return HSSFFormulaParser.toFormulaString(this.workbook, parsedExpression);
    }
}

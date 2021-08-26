// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.hssf.usermodel;

import org.apache.poi.hssf.record.CFRuleRecord;
import org.apache.poi.ss.usermodel.ConditionalFormattingRule;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.Region;
import org.apache.poi.hssf.record.aggregates.CFRecordsAggregate;
import org.apache.poi.ss.usermodel.ConditionalFormatting;

public final class HSSFConditionalFormatting implements ConditionalFormatting
{
    private final HSSFWorkbook _workbook;
    private final CFRecordsAggregate cfAggregate;
    
    HSSFConditionalFormatting(final HSSFWorkbook workbook, final CFRecordsAggregate cfAggregate) {
        if (workbook == null) {
            throw new IllegalArgumentException("workbook must not be null");
        }
        if (cfAggregate == null) {
            throw new IllegalArgumentException("cfAggregate must not be null");
        }
        this._workbook = workbook;
        this.cfAggregate = cfAggregate;
    }
    
    CFRecordsAggregate getCFRecordsAggregate() {
        return this.cfAggregate;
    }
    
    @Deprecated
    public Region[] getFormattingRegions() {
        final CellRangeAddress[] cellRanges = this.getFormattingRanges();
        return Region.convertCellRangesToRegions(cellRanges);
    }
    
    @Override
    public CellRangeAddress[] getFormattingRanges() {
        return this.cfAggregate.getHeader().getCellRanges();
    }
    
    public void setRule(final int idx, final HSSFConditionalFormattingRule cfRule) {
        this.cfAggregate.setRule(idx, cfRule.getCfRuleRecord());
    }
    
    @Override
    public void setRule(final int idx, final ConditionalFormattingRule cfRule) {
        this.setRule(idx, (HSSFConditionalFormattingRule)cfRule);
    }
    
    public void addRule(final HSSFConditionalFormattingRule cfRule) {
        this.cfAggregate.addRule(cfRule.getCfRuleRecord());
    }
    
    @Override
    public void addRule(final ConditionalFormattingRule cfRule) {
        this.addRule((HSSFConditionalFormattingRule)cfRule);
    }
    
    @Override
    public HSSFConditionalFormattingRule getRule(final int idx) {
        final CFRuleRecord ruleRecord = this.cfAggregate.getRule(idx);
        return new HSSFConditionalFormattingRule(this._workbook, ruleRecord);
    }
    
    @Override
    public int getNumberOfRules() {
        return this.cfAggregate.getNumberOfRules();
    }
    
    @Override
    public String toString() {
        return this.cfAggregate.toString();
    }
}

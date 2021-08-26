// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.hssf.usermodel;

import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;

public class HSSFCreationHelper implements CreationHelper
{
    private HSSFWorkbook workbook;
    private HSSFDataFormat dataFormat;
    
    HSSFCreationHelper(final HSSFWorkbook wb) {
        this.workbook = wb;
        this.dataFormat = new HSSFDataFormat(this.workbook.getWorkbook());
    }
    
    @Override
    public HSSFRichTextString createRichTextString(final String text) {
        return new HSSFRichTextString(text);
    }
    
    @Override
    public HSSFDataFormat createDataFormat() {
        return this.dataFormat;
    }
    
    @Override
    public HSSFHyperlink createHyperlink(final int type) {
        return new HSSFHyperlink(type);
    }
    
    @Override
    public HSSFFormulaEvaluator createFormulaEvaluator() {
        return new HSSFFormulaEvaluator(this.workbook);
    }
    
    @Override
    public HSSFClientAnchor createClientAnchor() {
        return new HSSFClientAnchor();
    }
}

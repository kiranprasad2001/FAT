// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.hssf.usermodel;

import org.apache.poi.hssf.record.HeaderRecord;
import org.apache.poi.hssf.record.aggregates.PageSettingsBlock;
import org.apache.poi.ss.usermodel.Header;

public final class HSSFHeader extends HeaderFooter implements Header
{
    private final PageSettingsBlock _psb;
    
    protected HSSFHeader(final PageSettingsBlock psb) {
        this._psb = psb;
    }
    
    @Override
    protected String getRawText() {
        final HeaderRecord hf = this._psb.getHeader();
        if (hf == null) {
            return "";
        }
        return hf.getText();
    }
    
    @Override
    protected void setHeaderFooterText(final String text) {
        HeaderRecord hfr = this._psb.getHeader();
        if (hfr == null) {
            hfr = new HeaderRecord(text);
            this._psb.setHeader(hfr);
        }
        else {
            hfr.setText(text);
        }
    }
}

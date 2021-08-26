// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.hssf.usermodel;

import org.apache.poi.hssf.record.FooterRecord;
import org.apache.poi.hssf.record.aggregates.PageSettingsBlock;
import org.apache.poi.ss.usermodel.Footer;

public final class HSSFFooter extends HeaderFooter implements Footer
{
    private final PageSettingsBlock _psb;
    
    protected HSSFFooter(final PageSettingsBlock psb) {
        this._psb = psb;
    }
    
    @Override
    protected String getRawText() {
        final FooterRecord hf = this._psb.getFooter();
        if (hf == null) {
            return "";
        }
        return hf.getText();
    }
    
    @Override
    protected void setHeaderFooterText(final String text) {
        FooterRecord hfr = this._psb.getFooter();
        if (hfr == null) {
            hfr = new FooterRecord(text);
            this._psb.setFooter(hfr);
        }
        else {
            hfr.setText(text);
        }
    }
}

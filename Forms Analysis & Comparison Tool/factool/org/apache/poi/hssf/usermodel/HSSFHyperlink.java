// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.hssf.usermodel;

import org.apache.poi.hssf.record.HyperlinkRecord;
import org.apache.poi.ss.usermodel.Hyperlink;

public class HSSFHyperlink implements Hyperlink
{
    public static final int LINK_URL = 1;
    public static final int LINK_DOCUMENT = 2;
    public static final int LINK_EMAIL = 3;
    public static final int LINK_FILE = 4;
    protected HyperlinkRecord record;
    protected int link_type;
    
    public HSSFHyperlink(final int type) {
        this.record = null;
        this.link_type = type;
        this.record = new HyperlinkRecord();
        switch (type) {
            case 1:
            case 3: {
                this.record.newUrlLink();
                break;
            }
            case 4: {
                this.record.newFileLink();
                break;
            }
            case 2: {
                this.record.newDocumentLink();
                break;
            }
        }
    }
    
    protected HSSFHyperlink(final HyperlinkRecord record) {
        this.record = null;
        this.record = record;
        if (record.isFileLink()) {
            this.link_type = 4;
        }
        else if (record.isDocumentLink()) {
            this.link_type = 2;
        }
        else if (record.getAddress() != null && record.getAddress().startsWith("mailto:")) {
            this.link_type = 3;
        }
        else {
            this.link_type = 1;
        }
    }
    
    @Override
    public int getFirstRow() {
        return this.record.getFirstRow();
    }
    
    @Override
    public void setFirstRow(final int row) {
        this.record.setFirstRow(row);
    }
    
    @Override
    public int getLastRow() {
        return this.record.getLastRow();
    }
    
    @Override
    public void setLastRow(final int row) {
        this.record.setLastRow(row);
    }
    
    @Override
    public int getFirstColumn() {
        return this.record.getFirstColumn();
    }
    
    @Override
    public void setFirstColumn(final int col) {
        this.record.setFirstColumn((short)col);
    }
    
    @Override
    public int getLastColumn() {
        return this.record.getLastColumn();
    }
    
    @Override
    public void setLastColumn(final int col) {
        this.record.setLastColumn((short)col);
    }
    
    @Override
    public String getAddress() {
        return this.record.getAddress();
    }
    
    public String getTextMark() {
        return this.record.getTextMark();
    }
    
    public void setTextMark(final String textMark) {
        this.record.setTextMark(textMark);
    }
    
    public String getShortFilename() {
        return this.record.getShortFilename();
    }
    
    public void setShortFilename(final String shortFilename) {
        this.record.setShortFilename(shortFilename);
    }
    
    @Override
    public void setAddress(final String address) {
        this.record.setAddress(address);
    }
    
    @Override
    public String getLabel() {
        return this.record.getLabel();
    }
    
    @Override
    public void setLabel(final String label) {
        this.record.setLabel(label);
    }
    
    @Override
    public int getType() {
        return this.link_type;
    }
}

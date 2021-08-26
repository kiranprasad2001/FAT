// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula;

public class SheetIdentifier
{
    public String _bookName;
    public NameIdentifier _sheetIdentifier;
    
    public SheetIdentifier(final String bookName, final NameIdentifier sheetIdentifier) {
        this._bookName = bookName;
        this._sheetIdentifier = sheetIdentifier;
    }
    
    public String getBookName() {
        return this._bookName;
    }
    
    public NameIdentifier getSheetIdentifier() {
        return this._sheetIdentifier;
    }
    
    protected void asFormulaString(final StringBuffer sb) {
        if (this._bookName != null) {
            sb.append(" [").append(this._sheetIdentifier.getName()).append("]");
        }
        if (this._sheetIdentifier.isQuoted()) {
            sb.append("'").append(this._sheetIdentifier.getName()).append("'");
        }
        else {
            sb.append(this._sheetIdentifier.getName());
        }
    }
    
    public String asFormulaString() {
        final StringBuffer sb = new StringBuffer(32);
        this.asFormulaString(sb);
        return sb.toString();
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer(64);
        sb.append(this.getClass().getName());
        sb.append(" [");
        this.asFormulaString(sb);
        sb.append("]");
        return sb.toString();
    }
}

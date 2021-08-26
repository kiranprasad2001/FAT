// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.hssf.usermodel;

import org.apache.poi.ss.usermodel.Font;
import java.util.Iterator;
import org.apache.poi.hssf.record.LabelSSTRecord;
import org.apache.poi.hssf.model.InternalWorkbook;
import org.apache.poi.hssf.record.common.UnicodeString;
import org.apache.poi.ss.usermodel.RichTextString;

public final class HSSFRichTextString implements Comparable<HSSFRichTextString>, RichTextString
{
    public static final short NO_FONT = 0;
    private UnicodeString _string;
    private InternalWorkbook _book;
    private LabelSSTRecord _record;
    
    public HSSFRichTextString() {
        this("");
    }
    
    public HSSFRichTextString(final String string) {
        if (string == null) {
            this._string = new UnicodeString("");
        }
        else {
            this._string = new UnicodeString(string);
        }
    }
    
    HSSFRichTextString(final InternalWorkbook book, final LabelSSTRecord record) {
        this.setWorkbookReferences(book, record);
        this._string = book.getSSTString(record.getSSTIndex());
    }
    
    void setWorkbookReferences(final InternalWorkbook book, final LabelSSTRecord record) {
        this._book = book;
        this._record = record;
    }
    
    private UnicodeString cloneStringIfRequired() {
        if (this._book == null) {
            return this._string;
        }
        final UnicodeString s = (UnicodeString)this._string.clone();
        return s;
    }
    
    private void addToSSTIfRequired() {
        if (this._book != null) {
            final int index = this._book.addSSTString(this._string);
            this._record.setSSTIndex(index);
            this._string = this._book.getSSTString(index);
        }
    }
    
    @Override
    public void applyFont(final int startIndex, final int endIndex, final short fontIndex) {
        if (startIndex > endIndex) {
            throw new IllegalArgumentException("Start index must be less than end index.");
        }
        if (startIndex < 0 || endIndex > this.length()) {
            throw new IllegalArgumentException("Start and end index not in range.");
        }
        if (startIndex == endIndex) {
            return;
        }
        short currentFont = 0;
        if (endIndex != this.length()) {
            currentFont = this.getFontAtIndex(endIndex);
        }
        this._string = this.cloneStringIfRequired();
        final Iterator<UnicodeString.FormatRun> formatting = this._string.formatIterator();
        if (formatting != null) {
            while (formatting.hasNext()) {
                final UnicodeString.FormatRun r = formatting.next();
                if (r.getCharacterPos() >= startIndex && r.getCharacterPos() < endIndex) {
                    formatting.remove();
                }
            }
        }
        this._string.addFormatRun(new UnicodeString.FormatRun((short)startIndex, fontIndex));
        if (endIndex != this.length()) {
            this._string.addFormatRun(new UnicodeString.FormatRun((short)endIndex, currentFont));
        }
        this.addToSSTIfRequired();
    }
    
    @Override
    public void applyFont(final int startIndex, final int endIndex, final Font font) {
        this.applyFont(startIndex, endIndex, ((HSSFFont)font).getIndex());
    }
    
    @Override
    public void applyFont(final Font font) {
        this.applyFont(0, this._string.getCharCount(), font);
    }
    
    @Override
    public void clearFormatting() {
        (this._string = this.cloneStringIfRequired()).clearFormatting();
        this.addToSSTIfRequired();
    }
    
    @Override
    public String getString() {
        return this._string.getString();
    }
    
    UnicodeString getUnicodeString() {
        return this.cloneStringIfRequired();
    }
    
    UnicodeString getRawUnicodeString() {
        return this._string;
    }
    
    void setUnicodeString(final UnicodeString str) {
        this._string = str;
    }
    
    @Override
    public int length() {
        return this._string.getCharCount();
    }
    
    public short getFontAtIndex(final int index) {
        final int size = this._string.getFormatRunCount();
        UnicodeString.FormatRun currentRun = null;
        for (int i = 0; i < size; ++i) {
            final UnicodeString.FormatRun r = this._string.getFormatRun(i);
            if (r.getCharacterPos() > index) {
                break;
            }
            currentRun = r;
        }
        if (currentRun == null) {
            return 0;
        }
        return currentRun.getFontIndex();
    }
    
    @Override
    public int numFormattingRuns() {
        return this._string.getFormatRunCount();
    }
    
    @Override
    public int getIndexOfFormattingRun(final int index) {
        final UnicodeString.FormatRun r = this._string.getFormatRun(index);
        return r.getCharacterPos();
    }
    
    public short getFontOfFormattingRun(final int index) {
        final UnicodeString.FormatRun r = this._string.getFormatRun(index);
        return r.getFontIndex();
    }
    
    @Override
    public int compareTo(final HSSFRichTextString r) {
        return this._string.compareTo(r._string);
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof HSSFRichTextString && this._string.equals(((HSSFRichTextString)o)._string);
    }
    
    @Override
    public int hashCode() {
        assert false : "hashCode not designed";
        return 42;
    }
    
    @Override
    public String toString() {
        return this._string.toString();
    }
    
    @Override
    public void applyFont(final short fontIndex) {
        this.applyFont(0, this._string.getCharCount(), fontIndex);
    }
}

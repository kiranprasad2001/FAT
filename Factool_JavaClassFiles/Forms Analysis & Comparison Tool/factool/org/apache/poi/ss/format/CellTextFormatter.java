// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.format;

import java.util.regex.Matcher;

public class CellTextFormatter extends CellFormatter
{
    private final int[] textPos;
    private final String desc;
    static final CellFormatter SIMPLE_TEXT;
    
    public CellTextFormatter(final String format) {
        super(format);
        final int[] numPlaces = { 0 };
        this.desc = CellFormatPart.parseFormat(format, CellFormatType.TEXT, new CellFormatPart.PartHandler() {
            @Override
            public String handlePart(final Matcher m, final String part, final CellFormatType type, final StringBuffer desc) {
                if (part.equals("@")) {
                    final int[] val$numPlaces = numPlaces;
                    final int n = 0;
                    ++val$numPlaces[n];
                    return "\u0000";
                }
                return null;
            }
        }).toString();
        this.textPos = new int[numPlaces[0]];
        int pos = this.desc.length() - 1;
        for (int i = 0; i < this.textPos.length; ++i) {
            this.textPos[i] = this.desc.lastIndexOf("\u0000", pos);
            pos = this.textPos[i] - 1;
        }
    }
    
    @Override
    public void formatValue(final StringBuffer toAppendTo, final Object obj) {
        final int start = toAppendTo.length();
        String text = obj.toString();
        if (obj instanceof Boolean) {
            text = text.toUpperCase();
        }
        toAppendTo.append(this.desc);
        for (int i = 0; i < this.textPos.length; ++i) {
            final int pos = start + this.textPos[i];
            toAppendTo.replace(pos, pos + 1, text);
        }
    }
    
    @Override
    public void simpleValue(final StringBuffer toAppendTo, final Object value) {
        CellTextFormatter.SIMPLE_TEXT.formatValue(toAppendTo, value);
    }
    
    static {
        SIMPLE_TEXT = new CellTextFormatter("@");
    }
}

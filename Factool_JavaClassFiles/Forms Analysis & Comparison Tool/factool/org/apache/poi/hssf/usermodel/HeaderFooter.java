// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.hssf.usermodel;

public abstract class HeaderFooter implements org.apache.poi.ss.usermodel.HeaderFooter
{
    protected HeaderFooter() {
    }
    
    protected abstract String getRawText();
    
    private String[] splitParts() {
        String text = this.getRawText();
        String _left = "";
        String _center = "";
        String _right = "";
    Label_0279:
        while (text.length() > 1) {
            if (text.charAt(0) != '&') {
                _center = text;
                break;
            }
            int pos = text.length();
            switch (text.charAt(1)) {
                case 'L': {
                    if (text.indexOf("&C") >= 0) {
                        pos = Math.min(pos, text.indexOf("&C"));
                    }
                    if (text.indexOf("&R") >= 0) {
                        pos = Math.min(pos, text.indexOf("&R"));
                    }
                    _left = text.substring(2, pos);
                    text = text.substring(pos);
                    continue;
                }
                case 'C': {
                    if (text.indexOf("&L") >= 0) {
                        pos = Math.min(pos, text.indexOf("&L"));
                    }
                    if (text.indexOf("&R") >= 0) {
                        pos = Math.min(pos, text.indexOf("&R"));
                    }
                    _center = text.substring(2, pos);
                    text = text.substring(pos);
                    continue;
                }
                case 'R': {
                    if (text.indexOf("&C") >= 0) {
                        pos = Math.min(pos, text.indexOf("&C"));
                    }
                    if (text.indexOf("&L") >= 0) {
                        pos = Math.min(pos, text.indexOf("&L"));
                    }
                    _right = text.substring(2, pos);
                    text = text.substring(pos);
                    continue;
                }
                default: {
                    _center = text;
                    break Label_0279;
                }
            }
        }
        return new String[] { _left, _center, _right };
    }
    
    @Override
    public final String getLeft() {
        return this.splitParts()[0];
    }
    
    @Override
    public final void setLeft(final String newLeft) {
        this.updatePart(0, newLeft);
    }
    
    @Override
    public final String getCenter() {
        return this.splitParts()[1];
    }
    
    @Override
    public final void setCenter(final String newCenter) {
        this.updatePart(1, newCenter);
    }
    
    @Override
    public final String getRight() {
        return this.splitParts()[2];
    }
    
    @Override
    public final void setRight(final String newRight) {
        this.updatePart(2, newRight);
    }
    
    private void updatePart(final int partIndex, final String newValue) {
        final String[] parts = this.splitParts();
        parts[partIndex] = ((newValue == null) ? "" : newValue);
        this.updateHeaderFooterText(parts);
    }
    
    private void updateHeaderFooterText(final String[] parts) {
        final String _left = parts[0];
        final String _center = parts[1];
        final String _right = parts[2];
        if (_center.length() < 1 && _left.length() < 1 && _right.length() < 1) {
            this.setHeaderFooterText("");
            return;
        }
        final StringBuilder sb = new StringBuilder(64);
        sb.append("&C");
        sb.append(_center);
        sb.append("&L");
        sb.append(_left);
        sb.append("&R");
        sb.append(_right);
        final String text = sb.toString();
        this.setHeaderFooterText(text);
    }
    
    protected abstract void setHeaderFooterText(final String p0);
    
    public static String fontSize(final short size) {
        return "&" + size;
    }
    
    public static String font(final String font, final String style) {
        return "&\"" + font + "," + style + "\"";
    }
    
    public static String page() {
        return MarkupTag.PAGE_FIELD.getRepresentation();
    }
    
    public static String numPages() {
        return MarkupTag.NUM_PAGES_FIELD.getRepresentation();
    }
    
    public static String date() {
        return MarkupTag.DATE_FIELD.getRepresentation();
    }
    
    public static String time() {
        return MarkupTag.TIME_FIELD.getRepresentation();
    }
    
    public static String file() {
        return MarkupTag.FILE_FIELD.getRepresentation();
    }
    
    public static String tab() {
        return MarkupTag.SHEET_NAME_FIELD.getRepresentation();
    }
    
    public static String startBold() {
        return MarkupTag.BOLD_FIELD.getRepresentation();
    }
    
    public static String endBold() {
        return MarkupTag.BOLD_FIELD.getRepresentation();
    }
    
    public static String startUnderline() {
        return MarkupTag.UNDERLINE_FIELD.getRepresentation();
    }
    
    public static String endUnderline() {
        return MarkupTag.UNDERLINE_FIELD.getRepresentation();
    }
    
    public static String startDoubleUnderline() {
        return MarkupTag.DOUBLE_UNDERLINE_FIELD.getRepresentation();
    }
    
    public static String endDoubleUnderline() {
        return MarkupTag.DOUBLE_UNDERLINE_FIELD.getRepresentation();
    }
    
    public static String stripFields(final String pText) {
        if (pText == null || pText.length() == 0) {
            return pText;
        }
        String text = pText;
        for (final MarkupTag mt : MarkupTag.values()) {
            int pos;
            for (String seq = mt.getRepresentation(); (pos = text.indexOf(seq)) > -1; text = text.substring(0, pos) + text.substring(pos + seq.length())) {}
        }
        text = text.replaceAll("\\&\\d+", "");
        text = text.replaceAll("\\&\".*?,.*?\"", "");
        return text;
    }
    
    private enum MarkupTag
    {
        SHEET_NAME_FIELD("&A", false), 
        DATE_FIELD("&D", false), 
        FILE_FIELD("&F", false), 
        FULL_FILE_FIELD("&Z", false), 
        PAGE_FIELD("&P", false), 
        TIME_FIELD("&T", false), 
        NUM_PAGES_FIELD("&N", false), 
        PICTURE_FIELD("&G", false), 
        BOLD_FIELD("&B", true), 
        ITALIC_FIELD("&I", true), 
        STRIKETHROUGH_FIELD("&S", true), 
        SUBSCRIPT_FIELD("&Y", true), 
        SUPERSCRIPT_FIELD("&X", true), 
        UNDERLINE_FIELD("&U", true), 
        DOUBLE_UNDERLINE_FIELD("&E", true);
        
        private final String _representation;
        private final boolean _occursInPairs;
        
        private MarkupTag(final String sequence, final boolean occursInPairs) {
            this._representation = sequence;
            this._occursInPairs = occursInPairs;
        }
        
        public String getRepresentation() {
            return this._representation;
        }
        
        public boolean occursPairs() {
            return this._occursInPairs;
        }
    }
}

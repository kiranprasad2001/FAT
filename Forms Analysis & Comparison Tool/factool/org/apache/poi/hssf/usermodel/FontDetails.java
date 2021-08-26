// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.hssf.usermodel;

import java.util.StringTokenizer;
import java.util.Properties;
import java.util.HashMap;
import java.util.Map;

public class FontDetails
{
    private String _fontName;
    private int _height;
    private final Map<Character, Integer> charWidths;
    
    public FontDetails(final String fontName, final int height) {
        this.charWidths = new HashMap<Character, Integer>();
        this._fontName = fontName;
        this._height = height;
    }
    
    public String getFontName() {
        return this._fontName;
    }
    
    public int getHeight() {
        return this._height;
    }
    
    public void addChar(final char c, final int width) {
        this.charWidths.put(c, width);
    }
    
    public int getCharWidth(final char c) {
        final Integer widthInteger = this.charWidths.get(c);
        if (widthInteger == null) {
            return ('W' == c) ? 0 : this.getCharWidth('W');
        }
        return widthInteger;
    }
    
    public void addChars(final char[] characters, final int[] widths) {
        for (int i = 0; i < characters.length; ++i) {
            this.charWidths.put(characters[i], widths[i]);
        }
    }
    
    protected static String buildFontHeightProperty(final String fontName) {
        return "font." + fontName + ".height";
    }
    
    protected static String buildFontWidthsProperty(final String fontName) {
        return "font." + fontName + ".widths";
    }
    
    protected static String buildFontCharactersProperty(final String fontName) {
        return "font." + fontName + ".characters";
    }
    
    public static FontDetails create(final String fontName, final Properties fontMetricsProps) {
        final String heightStr = fontMetricsProps.getProperty(buildFontHeightProperty(fontName));
        final String widthsStr = fontMetricsProps.getProperty(buildFontWidthsProperty(fontName));
        final String charactersStr = fontMetricsProps.getProperty(buildFontCharactersProperty(fontName));
        if (heightStr == null || widthsStr == null || charactersStr == null) {
            throw new IllegalArgumentException("The supplied FontMetrics doesn't know about the font '" + fontName + "', so we can't use it. Please add it to your font metrics file (see StaticFontMetrics.getFontDetails");
        }
        final int height = Integer.parseInt(heightStr);
        final FontDetails d = new FontDetails(fontName, height);
        final String[] charactersStrArray = split(charactersStr, ",", -1);
        final String[] widthsStrArray = split(widthsStr, ",", -1);
        if (charactersStrArray.length != widthsStrArray.length) {
            throw new RuntimeException("Number of characters does not number of widths for font " + fontName);
        }
        for (int i = 0; i < widthsStrArray.length; ++i) {
            if (charactersStrArray[i].length() != 0) {
                d.addChar(charactersStrArray[i].charAt(0), Integer.parseInt(widthsStrArray[i]));
            }
        }
        return d;
    }
    
    public int getStringWidth(final String str) {
        int width = 0;
        for (int i = 0; i < str.length(); ++i) {
            width += this.getCharWidth(str.charAt(i));
        }
        return width;
    }
    
    private static String[] split(final String text, final String separator, final int max) {
        final StringTokenizer tok = new StringTokenizer(text, separator);
        int listSize = tok.countTokens();
        if (max != -1 && listSize > max) {
            listSize = max;
        }
        final String[] list = new String[listSize];
        int i = 0;
        while (tok.hasMoreTokens()) {
            if (max != -1 && i == listSize - 1) {
                final StringBuffer buf = new StringBuffer(text.length() * (listSize - i) / listSize);
                while (tok.hasMoreTokens()) {
                    buf.append(tok.nextToken());
                    if (tok.hasMoreTokens()) {
                        buf.append(separator);
                    }
                }
                list[i] = buf.toString().trim();
                break;
            }
            list[i] = tok.nextToken().trim();
            ++i;
        }
        return list;
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.usermodel;

public enum FontFamily
{
    NOT_APPLICABLE(0), 
    ROMAN(1), 
    SWISS(2), 
    MODERN(3), 
    SCRIPT(4), 
    DECORATIVE(5);
    
    private int family;
    private static FontFamily[] _table;
    
    private FontFamily(final int value) {
        this.family = value;
    }
    
    public int getValue() {
        return this.family;
    }
    
    public static FontFamily valueOf(final int family) {
        return FontFamily._table[family];
    }
    
    static {
        FontFamily._table = new FontFamily[6];
        for (final FontFamily c : values()) {
            FontFamily._table[c.getValue()] = c;
        }
    }
}

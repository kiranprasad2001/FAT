// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.usermodel;

public enum FontScheme
{
    NONE(1), 
    MAJOR(2), 
    MINOR(3);
    
    private int value;
    private static FontScheme[] _table;
    
    private FontScheme(final int val) {
        this.value = val;
    }
    
    public int getValue() {
        return this.value;
    }
    
    public static FontScheme valueOf(final int value) {
        return FontScheme._table[value];
    }
    
    static {
        FontScheme._table = new FontScheme[4];
        for (final FontScheme c : values()) {
            FontScheme._table[c.getValue()] = c;
        }
    }
}

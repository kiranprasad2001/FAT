// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.usermodel;

public enum PrintOrientation
{
    DEFAULT(1), 
    PORTRAIT(2), 
    LANDSCAPE(3);
    
    private int orientation;
    private static PrintOrientation[] _table;
    
    private PrintOrientation(final int orientation) {
        this.orientation = orientation;
    }
    
    public int getValue() {
        return this.orientation;
    }
    
    public static PrintOrientation valueOf(final int value) {
        return PrintOrientation._table[value];
    }
    
    static {
        PrintOrientation._table = new PrintOrientation[4];
        for (final PrintOrientation c : values()) {
            PrintOrientation._table[c.getValue()] = c;
        }
    }
}

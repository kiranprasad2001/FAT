// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.usermodel;

public enum PrintCellComments
{
    NONE(1), 
    AS_DISPLAYED(2), 
    AT_END(3);
    
    private int comments;
    private static PrintCellComments[] _table;
    
    private PrintCellComments(final int comments) {
        this.comments = comments;
    }
    
    public int getValue() {
        return this.comments;
    }
    
    public static PrintCellComments valueOf(final int value) {
        return PrintCellComments._table[value];
    }
    
    static {
        PrintCellComments._table = new PrintCellComments[4];
        for (final PrintCellComments c : values()) {
            PrintCellComments._table[c.getValue()] = c;
        }
    }
}

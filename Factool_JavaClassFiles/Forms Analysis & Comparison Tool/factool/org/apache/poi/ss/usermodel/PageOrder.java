// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.usermodel;

public enum PageOrder
{
    DOWN_THEN_OVER(1), 
    OVER_THEN_DOWN(2);
    
    private int order;
    private static PageOrder[] _table;
    
    private PageOrder(final int order) {
        this.order = order;
    }
    
    public int getValue() {
        return this.order;
    }
    
    public static PageOrder valueOf(final int value) {
        return PageOrder._table[value];
    }
    
    static {
        PageOrder._table = new PageOrder[3];
        for (final PageOrder c : values()) {
            PageOrder._table[c.getValue()] = c;
        }
    }
}

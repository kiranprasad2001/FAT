// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.usermodel;

public enum FontUnderline
{
    SINGLE(1), 
    DOUBLE(2), 
    SINGLE_ACCOUNTING(3), 
    DOUBLE_ACCOUNTING(4), 
    NONE(5);
    
    private int value;
    private static FontUnderline[] _table;
    
    private FontUnderline(final int val) {
        this.value = val;
    }
    
    public int getValue() {
        return this.value;
    }
    
    public byte getByteValue() {
        switch (this) {
            case DOUBLE: {
                return 2;
            }
            case DOUBLE_ACCOUNTING: {
                return 34;
            }
            case SINGLE_ACCOUNTING: {
                return 33;
            }
            case NONE: {
                return 0;
            }
            case SINGLE: {
                return 1;
            }
            default: {
                return 1;
            }
        }
    }
    
    public static FontUnderline valueOf(final int value) {
        return FontUnderline._table[value];
    }
    
    public static FontUnderline valueOf(final byte value) {
        FontUnderline val = null;
        switch (value) {
            case 2: {
                val = FontUnderline.DOUBLE;
                break;
            }
            case 34: {
                val = FontUnderline.DOUBLE_ACCOUNTING;
                break;
            }
            case 33: {
                val = FontUnderline.SINGLE_ACCOUNTING;
                break;
            }
            case 1: {
                val = FontUnderline.SINGLE;
                break;
            }
            default: {
                val = FontUnderline.NONE;
                break;
            }
        }
        return val;
    }
    
    static {
        FontUnderline._table = new FontUnderline[6];
        for (final FontUnderline c : values()) {
            FontUnderline._table[c.getValue()] = c;
        }
    }
}

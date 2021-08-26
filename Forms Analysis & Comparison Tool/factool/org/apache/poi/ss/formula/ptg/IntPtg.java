// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.ptg;

import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.LittleEndianInput;

public final class IntPtg extends ScalarConstantPtg
{
    private static final int MIN_VALUE = 0;
    private static final int MAX_VALUE = 65535;
    public static final int SIZE = 3;
    public static final byte sid = 30;
    private final int field_1_value;
    
    public static boolean isInRange(final int i) {
        return i >= 0 && i <= 65535;
    }
    
    public IntPtg(final LittleEndianInput in) {
        this(in.readUShort());
    }
    
    public IntPtg(final int value) {
        if (!isInRange(value)) {
            throw new IllegalArgumentException("value is out of range: " + value);
        }
        this.field_1_value = value;
    }
    
    public int getValue() {
        return this.field_1_value;
    }
    
    @Override
    public void write(final LittleEndianOutput out) {
        out.writeByte(30 + this.getPtgClass());
        out.writeShort(this.getValue());
    }
    
    @Override
    public int getSize() {
        return 3;
    }
    
    @Override
    public String toFormulaString() {
        return String.valueOf(this.getValue());
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.ptg;

import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.LittleEndianInput;

public final class BoolPtg extends ScalarConstantPtg
{
    public static final int SIZE = 2;
    public static final byte sid = 29;
    private static final BoolPtg FALSE;
    private static final BoolPtg TRUE;
    private final boolean _value;
    
    private BoolPtg(final boolean b) {
        this._value = b;
    }
    
    public static BoolPtg valueOf(final boolean b) {
        return b ? BoolPtg.TRUE : BoolPtg.FALSE;
    }
    
    public static BoolPtg read(final LittleEndianInput in) {
        return valueOf(in.readByte() == 1);
    }
    
    public boolean getValue() {
        return this._value;
    }
    
    @Override
    public void write(final LittleEndianOutput out) {
        out.writeByte(29 + this.getPtgClass());
        out.writeByte(this._value ? 1 : 0);
    }
    
    @Override
    public int getSize() {
        return 2;
    }
    
    @Override
    public String toFormulaString() {
        return this._value ? "TRUE" : "FALSE";
    }
    
    static {
        FALSE = new BoolPtg(false);
        TRUE = new BoolPtg(true);
    }
}

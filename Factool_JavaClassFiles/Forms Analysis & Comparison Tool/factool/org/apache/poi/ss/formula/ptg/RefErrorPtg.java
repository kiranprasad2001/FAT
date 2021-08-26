// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.ptg;

import org.apache.poi.ss.usermodel.ErrorConstants;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.LittleEndianInput;

public final class RefErrorPtg extends OperandPtg
{
    private static final int SIZE = 5;
    public static final byte sid = 42;
    private int field_1_reserved;
    
    public RefErrorPtg() {
        this.field_1_reserved = 0;
    }
    
    public RefErrorPtg(final LittleEndianInput in) {
        this.field_1_reserved = in.readInt();
    }
    
    @Override
    public String toString() {
        return this.getClass().getName();
    }
    
    @Override
    public void write(final LittleEndianOutput out) {
        out.writeByte(42 + this.getPtgClass());
        out.writeInt(this.field_1_reserved);
    }
    
    @Override
    public int getSize() {
        return 5;
    }
    
    @Override
    public String toFormulaString() {
        return ErrorConstants.getText(23);
    }
    
    @Override
    public byte getDefaultOperandClass() {
        return 0;
    }
}

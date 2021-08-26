// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.ptg;

import org.apache.poi.util.LittleEndianOutput;

public class UnknownPtg extends Ptg
{
    private short size;
    private final int _sid;
    
    public UnknownPtg(final int sid) {
        this.size = 1;
        this._sid = sid;
    }
    
    @Override
    public boolean isBaseToken() {
        return true;
    }
    
    @Override
    public void write(final LittleEndianOutput out) {
        out.writeByte(this._sid);
    }
    
    @Override
    public int getSize() {
        return this.size;
    }
    
    @Override
    public String toFormulaString() {
        return "UNKNOWN";
    }
    
    @Override
    public byte getDefaultOperandClass() {
        return 32;
    }
}

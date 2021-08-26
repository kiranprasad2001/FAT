// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.ptg;

public abstract class ControlPtg extends Ptg
{
    @Override
    public boolean isBaseToken() {
        return true;
    }
    
    @Override
    public final byte getDefaultOperandClass() {
        throw new IllegalStateException("Control tokens are not classified");
    }
}

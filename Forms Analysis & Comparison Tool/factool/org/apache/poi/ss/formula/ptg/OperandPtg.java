// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.ptg;

public abstract class OperandPtg extends Ptg implements Cloneable
{
    @Override
    public final boolean isBaseToken() {
        return false;
    }
    
    public final OperandPtg copy() {
        try {
            return (OperandPtg)this.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}

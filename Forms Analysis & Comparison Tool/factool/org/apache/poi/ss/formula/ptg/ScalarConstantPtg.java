// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.ptg;

public abstract class ScalarConstantPtg extends Ptg
{
    @Override
    public final boolean isBaseToken() {
        return true;
    }
    
    @Override
    public final byte getDefaultOperandClass() {
        return 32;
    }
    
    @Override
    public final String toString() {
        final StringBuffer sb = new StringBuffer(64);
        sb.append(this.getClass().getName()).append(" [");
        sb.append(this.toFormulaString());
        sb.append("]");
        return sb.toString();
    }
}

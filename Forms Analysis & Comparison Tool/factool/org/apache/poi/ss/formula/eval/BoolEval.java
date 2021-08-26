// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.eval;

public final class BoolEval implements NumericValueEval, StringValueEval
{
    private boolean _value;
    public static final BoolEval FALSE;
    public static final BoolEval TRUE;
    
    public static final BoolEval valueOf(final boolean b) {
        return b ? BoolEval.TRUE : BoolEval.FALSE;
    }
    
    private BoolEval(final boolean value) {
        this._value = value;
    }
    
    public boolean getBooleanValue() {
        return this._value;
    }
    
    @Override
    public double getNumberValue() {
        return this._value ? 1.0 : 0.0;
    }
    
    @Override
    public String getStringValue() {
        return this._value ? "TRUE" : "FALSE";
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(64);
        sb.append(this.getClass().getName()).append(" [");
        sb.append(this.getStringValue());
        sb.append("]");
        return sb.toString();
    }
    
    static {
        FALSE = new BoolEval(false);
        TRUE = new BoolEval(true);
    }
}

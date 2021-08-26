// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.eval;

import org.apache.poi.ss.formula.ptg.StringPtg;
import org.apache.poi.ss.formula.ptg.Ptg;

public final class StringEval implements StringValueEval
{
    public static final StringEval EMPTY_INSTANCE;
    private final String _value;
    
    public StringEval(final Ptg ptg) {
        this(((StringPtg)ptg).getValue());
    }
    
    public StringEval(final String value) {
        if (value == null) {
            throw new IllegalArgumentException("value must not be null");
        }
        this._value = value;
    }
    
    @Override
    public String getStringValue() {
        return this._value;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(64);
        sb.append(this.getClass().getName()).append(" [");
        sb.append(this._value);
        sb.append("]");
        return sb.toString();
    }
    
    static {
        EMPTY_INSTANCE = new StringEval("");
    }
}

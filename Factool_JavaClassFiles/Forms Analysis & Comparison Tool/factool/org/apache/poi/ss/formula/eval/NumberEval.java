// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.eval;

import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.ss.formula.ptg.NumberPtg;
import org.apache.poi.ss.formula.ptg.IntPtg;
import org.apache.poi.ss.formula.ptg.Ptg;

public final class NumberEval implements NumericValueEval, StringValueEval
{
    public static final NumberEval ZERO;
    private final double _value;
    private String _stringValue;
    
    public NumberEval(final Ptg ptg) {
        if (ptg == null) {
            throw new IllegalArgumentException("ptg must not be null");
        }
        if (ptg instanceof IntPtg) {
            this._value = ((IntPtg)ptg).getValue();
        }
        else {
            if (!(ptg instanceof NumberPtg)) {
                throw new IllegalArgumentException("bad argument type (" + ptg.getClass().getName() + ")");
            }
            this._value = ((NumberPtg)ptg).getValue();
        }
    }
    
    public NumberEval(final double value) {
        this._value = value;
    }
    
    @Override
    public double getNumberValue() {
        return this._value;
    }
    
    @Override
    public String getStringValue() {
        if (this._stringValue == null) {
            this._stringValue = NumberToTextConverter.toText(this._value);
        }
        return this._stringValue;
    }
    
    @Override
    public final String toString() {
        final StringBuffer sb = new StringBuffer(64);
        sb.append(this.getClass().getName()).append(" [");
        sb.append(this.getStringValue());
        sb.append("]");
        return sb.toString();
    }
    
    static {
        ZERO = new NumberEval(0.0);
    }
}

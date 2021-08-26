// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.ptg;

import org.apache.poi.ss.util.NumberToTextConverter;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.LittleEndianInput;

public final class NumberPtg extends ScalarConstantPtg
{
    public static final int SIZE = 9;
    public static final byte sid = 31;
    private final double field_1_value;
    
    public NumberPtg(final LittleEndianInput in) {
        this(in.readDouble());
    }
    
    public NumberPtg(final String value) {
        this(Double.parseDouble(value));
    }
    
    public NumberPtg(final double value) {
        this.field_1_value = value;
    }
    
    public double getValue() {
        return this.field_1_value;
    }
    
    @Override
    public void write(final LittleEndianOutput out) {
        out.writeByte(31 + this.getPtgClass());
        out.writeDouble(this.getValue());
    }
    
    @Override
    public int getSize() {
        return 9;
    }
    
    @Override
    public String toFormulaString() {
        return NumberToTextConverter.toText(this.field_1_value);
    }
}

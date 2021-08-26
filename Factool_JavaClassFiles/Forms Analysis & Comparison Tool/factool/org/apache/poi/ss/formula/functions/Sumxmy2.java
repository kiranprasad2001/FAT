// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.functions;

public final class Sumxmy2 extends XYNumericFunction
{
    private static final Accumulator XMinusYSquaredAccumulator;
    
    @Override
    protected Accumulator createAccumulator() {
        return Sumxmy2.XMinusYSquaredAccumulator;
    }
    
    static {
        XMinusYSquaredAccumulator = new Accumulator() {
            @Override
            public double accumulate(final double x, final double y) {
                final double xmy = x - y;
                return xmy * xmy;
            }
        };
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.functions;

public final class Sumx2my2 extends XYNumericFunction
{
    private static final Accumulator XSquaredMinusYSquaredAccumulator;
    
    @Override
    protected Accumulator createAccumulator() {
        return Sumx2my2.XSquaredMinusYSquaredAccumulator;
    }
    
    static {
        XSquaredMinusYSquaredAccumulator = new Accumulator() {
            @Override
            public double accumulate(final double x, final double y) {
                return x * x - y * y;
            }
        };
    }
}

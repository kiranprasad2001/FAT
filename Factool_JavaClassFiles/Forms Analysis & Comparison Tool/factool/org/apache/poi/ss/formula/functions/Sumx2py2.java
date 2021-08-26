// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.functions;

public final class Sumx2py2 extends XYNumericFunction
{
    private static final Accumulator XSquaredPlusYSquaredAccumulator;
    
    @Override
    protected Accumulator createAccumulator() {
        return Sumx2py2.XSquaredPlusYSquaredAccumulator;
    }
    
    static {
        XSquaredPlusYSquaredAccumulator = new Accumulator() {
            @Override
            public double accumulate(final double x, final double y) {
                return x * x + y * y;
            }
        };
    }
}

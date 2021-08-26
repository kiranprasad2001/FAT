// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.functions;

public final class Odd extends NumericFunction.OneArg
{
    private static final long PARITY_MASK = -2L;
    
    @Override
    protected double evaluate(final double d) {
        if (d == 0.0) {
            return 1.0;
        }
        if (d > 0.0) {
            return (double)calcOdd(d);
        }
        return (double)(-calcOdd(-d));
    }
    
    private static long calcOdd(final double d) {
        final double dpm1 = d + 1.0;
        final long x = (long)dpm1 & 0xFFFFFFFFFFFFFFFEL;
        if (x == dpm1) {
            return x - 1L;
        }
        return x + 1L;
    }
}

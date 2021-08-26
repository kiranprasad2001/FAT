// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.functions;

public final class Even extends NumericFunction.OneArg
{
    private static final long PARITY_MASK = -2L;
    
    @Override
    protected double evaluate(final double d) {
        if (d == 0.0) {
            return 0.0;
        }
        long result;
        if (d > 0.0) {
            result = calcEven(d);
        }
        else {
            result = -calcEven(-d);
        }
        return (double)result;
    }
    
    private static long calcEven(final double d) {
        final long x = (long)d & 0xFFFFFFFFFFFFFFFEL;
        if (x == d) {
            return x;
        }
        return x + 2L;
    }
}

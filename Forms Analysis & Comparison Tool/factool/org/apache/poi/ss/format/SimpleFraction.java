// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.format;

public class SimpleFraction
{
    private final int denominator;
    private final int numerator;
    
    public static SimpleFraction buildFractionExactDenominator(final double val, final int exactDenom) {
        final int num = (int)Math.round(val * exactDenom);
        return new SimpleFraction(num, exactDenom);
    }
    
    public static SimpleFraction buildFractionMaxDenominator(final double value, final int maxDenominator) {
        return buildFractionMaxDenominator(value, 0.0, maxDenominator, 100);
    }
    
    private static SimpleFraction buildFractionMaxDenominator(final double value, final double epsilon, final int maxDenominator, final int maxIterations) {
        final long overflow = 2147483647L;
        double r0 = value;
        long a0 = (long)Math.floor(r0);
        if (a0 > overflow) {
            throw new IllegalArgumentException("Overflow trying to convert " + value + " to fraction (" + a0 + "/" + 1L + ")");
        }
        if (Math.abs(a0 - value) < epsilon) {
            return new SimpleFraction((int)a0, 1);
        }
        long p0 = 1L;
        long q0 = 0L;
        long p2 = a0;
        long q2 = 1L;
        int n = 0;
        boolean stop = false;
        long p3;
        long q3;
        do {
            ++n;
            final double r2 = 1.0 / (r0 - a0);
            final long a2 = (long)Math.floor(r2);
            p3 = a2 * p2 + p0;
            q3 = a2 * q2 + q0;
            if (epsilon == 0.0 && maxDenominator > 0 && Math.abs(q3) > maxDenominator && Math.abs(q2) < maxDenominator) {
                return new SimpleFraction((int)p2, (int)q2);
            }
            if (p3 > overflow || q3 > overflow) {
                throw new RuntimeException("Overflow trying to convert " + value + " to fraction (" + p3 + "/" + q3 + ")");
            }
            final double convergent = p3 / (double)q3;
            if (n < maxIterations && Math.abs(convergent - value) > epsilon && q3 < maxDenominator) {
                p0 = p2;
                p2 = p3;
                q0 = q2;
                q2 = q3;
                a0 = a2;
                r0 = r2;
            }
            else {
                stop = true;
            }
        } while (!stop);
        if (n >= maxIterations) {
            throw new RuntimeException("Unable to convert " + value + " to fraction after " + maxIterations + " iterations");
        }
        if (q3 < maxDenominator) {
            return new SimpleFraction((int)p3, (int)q3);
        }
        return new SimpleFraction((int)p2, (int)q2);
    }
    
    public SimpleFraction(final int numerator, final int denominator) {
        this.numerator = numerator;
        this.denominator = denominator;
    }
    
    public int getDenominator() {
        return this.denominator;
    }
    
    public int getNumerator() {
        return this.numerator;
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.util;

import java.math.BigInteger;

final class ExpandedDouble
{
    private static final BigInteger BI_FRAC_MASK;
    private static final BigInteger BI_IMPLIED_FRAC_MSB;
    private final BigInteger _significand;
    private final int _binaryExponent;
    
    private static BigInteger getFrac(final long rawBits) {
        return BigInteger.valueOf(rawBits).and(ExpandedDouble.BI_FRAC_MASK).or(ExpandedDouble.BI_IMPLIED_FRAC_MSB).shiftLeft(11);
    }
    
    public static ExpandedDouble fromRawBitsAndExponent(final long rawBits, final int exp) {
        return new ExpandedDouble(getFrac(rawBits), exp);
    }
    
    public ExpandedDouble(final long rawBits) {
        final int biasedExp = (int)(rawBits >> 52);
        if (biasedExp == 0) {
            final BigInteger frac = BigInteger.valueOf(rawBits).and(ExpandedDouble.BI_FRAC_MASK);
            final int expAdj = 64 - frac.bitLength();
            this._significand = frac.shiftLeft(expAdj);
            this._binaryExponent = (biasedExp & 0x7FF) - 1023 - expAdj;
        }
        else {
            final BigInteger frac = getFrac(rawBits);
            this._significand = frac;
            this._binaryExponent = (biasedExp & 0x7FF) - 1023;
        }
    }
    
    ExpandedDouble(final BigInteger frac, final int binaryExp) {
        if (frac.bitLength() != 64) {
            throw new IllegalArgumentException("bad bit length");
        }
        this._significand = frac;
        this._binaryExponent = binaryExp;
    }
    
    public NormalisedDecimal normaliseBaseTen() {
        return NormalisedDecimal.create(this._significand, this._binaryExponent);
    }
    
    public int getBinaryExponent() {
        return this._binaryExponent;
    }
    
    public BigInteger getSignificand() {
        return this._significand;
    }
    
    static {
        BI_FRAC_MASK = BigInteger.valueOf(4503599627370495L);
        BI_IMPLIED_FRAC_MSB = BigInteger.valueOf(4503599627370496L);
    }
}

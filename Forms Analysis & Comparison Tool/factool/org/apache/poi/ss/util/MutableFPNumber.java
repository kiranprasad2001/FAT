// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.util;

import java.math.BigInteger;

final class MutableFPNumber
{
    private static final BigInteger BI_MIN_BASE;
    private static final BigInteger BI_MAX_BASE;
    private static final int C_64 = 64;
    private static final int MIN_PRECISION = 72;
    private BigInteger _significand;
    private int _binaryExponent;
    
    public MutableFPNumber(final BigInteger frac, final int binaryExponent) {
        this._significand = frac;
        this._binaryExponent = binaryExponent;
    }
    
    public MutableFPNumber copy() {
        return new MutableFPNumber(this._significand, this._binaryExponent);
    }
    
    public void normalise64bit() {
        int oldBitLen = this._significand.bitLength();
        int sc = oldBitLen - 64;
        if (sc == 0) {
            return;
        }
        if (sc < 0) {
            throw new IllegalStateException("Not enough precision");
        }
        this._binaryExponent += sc;
        if (sc > 32) {
            final int highShift = sc - 1 & 0xFFFFE0;
            this._significand = this._significand.shiftRight(highShift);
            sc -= highShift;
            oldBitLen -= highShift;
        }
        if (sc < 1) {
            throw new IllegalStateException();
        }
        this._significand = Rounder.round(this._significand, sc);
        if (this._significand.bitLength() > oldBitLen) {
            ++sc;
            ++this._binaryExponent;
        }
        this._significand = this._significand.shiftRight(sc);
    }
    
    public int get64BitNormalisedExponent() {
        return this._binaryExponent + this._significand.bitLength() - 64;
    }
    
    public boolean isBelowMaxRep() {
        final int sc = this._significand.bitLength() - 64;
        return this._significand.compareTo(MutableFPNumber.BI_MAX_BASE.shiftLeft(sc)) < 0;
    }
    
    public boolean isAboveMinRep() {
        final int sc = this._significand.bitLength() - 64;
        return this._significand.compareTo(MutableFPNumber.BI_MIN_BASE.shiftLeft(sc)) > 0;
    }
    
    public NormalisedDecimal createNormalisedDecimal(final int pow10) {
        final int missingUnderBits = this._binaryExponent - 39;
        final int fracPart = this._significand.intValue() << missingUnderBits & 0xFFFF80;
        final long wholePart = this._significand.shiftRight(64 - this._binaryExponent - 1).longValue();
        return new NormalisedDecimal(wholePart, fracPart, pow10);
    }
    
    public void multiplyByPowerOfTen(final int pow10) {
        final TenPower tp = TenPower.getInstance(Math.abs(pow10));
        if (pow10 < 0) {
            this.mulShift(tp._divisor, tp._divisorShift);
        }
        else {
            this.mulShift(tp._multiplicand, tp._multiplierShift);
        }
    }
    
    private void mulShift(final BigInteger multiplicand, final int multiplierShift) {
        this._significand = this._significand.multiply(multiplicand);
        this._binaryExponent += multiplierShift;
        final int sc = this._significand.bitLength() - 72 & 0xFFFFFFE0;
        if (sc > 0) {
            this._significand = this._significand.shiftRight(sc);
            this._binaryExponent += sc;
        }
    }
    
    public ExpandedDouble createExpandedDouble() {
        return new ExpandedDouble(this._significand, this._binaryExponent);
    }
    
    static {
        BI_MIN_BASE = new BigInteger("0B5E620F47FFFE666", 16);
        BI_MAX_BASE = new BigInteger("0E35FA9319FFFE000", 16);
    }
    
    private static final class Rounder
    {
        private static final BigInteger[] HALF_BITS;
        
        public static BigInteger round(final BigInteger bi, final int nBits) {
            if (nBits < 1) {
                return bi;
            }
            return bi.add(Rounder.HALF_BITS[nBits]);
        }
        
        static {
            final BigInteger[] bis = new BigInteger[33];
            long acc = 1L;
            for (int i = 1; i < bis.length; ++i) {
                bis[i] = BigInteger.valueOf(acc);
                acc <<= 1;
            }
            HALF_BITS = bis;
        }
    }
    
    private static final class TenPower
    {
        private static final BigInteger FIVE;
        private static final TenPower[] _cache;
        public final BigInteger _multiplicand;
        public final BigInteger _divisor;
        public final int _divisorShift;
        public final int _multiplierShift;
        
        private TenPower(final int index) {
            final BigInteger fivePowIndex = TenPower.FIVE.pow(index);
            int bitsDueToFiveFactors = fivePowIndex.bitLength();
            final int px = 80 + bitsDueToFiveFactors;
            final BigInteger fx = BigInteger.ONE.shiftLeft(px).divide(fivePowIndex);
            final int adj = fx.bitLength() - 80;
            this._divisor = fx.shiftRight(adj);
            bitsDueToFiveFactors -= adj;
            this._divisorShift = -(bitsDueToFiveFactors + index + 80);
            final int sc = fivePowIndex.bitLength() - 68;
            if (sc > 0) {
                this._multiplierShift = index + sc;
                this._multiplicand = fivePowIndex.shiftRight(sc);
            }
            else {
                this._multiplierShift = index;
                this._multiplicand = fivePowIndex;
            }
        }
        
        static TenPower getInstance(final int index) {
            TenPower result = TenPower._cache[index];
            if (result == null) {
                result = new TenPower(index);
                TenPower._cache[index] = result;
            }
            return result;
        }
        
        static {
            FIVE = new BigInteger("5");
            _cache = new TenPower[350];
        }
    }
}

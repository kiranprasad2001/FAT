// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.util;

public final class NumberComparer
{
    public static int compare(final double a, final double b) {
        final long rawBitsA = Double.doubleToLongBits(a);
        final long rawBitsB = Double.doubleToLongBits(b);
        final int biasedExponentA = IEEEDouble.getBiasedExponent(rawBitsA);
        final int biasedExponentB = IEEEDouble.getBiasedExponent(rawBitsB);
        if (biasedExponentA == 2047) {
            throw new IllegalArgumentException("Special double values are not allowed: " + toHex(a));
        }
        if (biasedExponentB == 2047) {
            throw new IllegalArgumentException("Special double values are not allowed: " + toHex(a));
        }
        final boolean aIsNegative = rawBitsA < 0L;
        final boolean bIsNegative = rawBitsB < 0L;
        if (aIsNegative != bIsNegative) {
            return aIsNegative ? -1 : 1;
        }
        int cmp = biasedExponentA - biasedExponentB;
        final int absExpDiff = Math.abs(cmp);
        if (absExpDiff > 1) {
            return aIsNegative ? (-cmp) : cmp;
        }
        if (absExpDiff != 1) {
            if (rawBitsA == rawBitsB) {
                return 0;
            }
        }
        if (biasedExponentA == 0) {
            if (biasedExponentB == 0) {
                return compareSubnormalNumbers(rawBitsA & 0xFFFFFFFFFFFFFL, rawBitsB & 0xFFFFFFFFFFFFFL, aIsNegative);
            }
            return -compareAcrossSubnormalThreshold(rawBitsB, rawBitsA, aIsNegative);
        }
        else {
            if (biasedExponentB == 0) {
                return compareAcrossSubnormalThreshold(rawBitsA, rawBitsB, aIsNegative);
            }
            final ExpandedDouble edA = ExpandedDouble.fromRawBitsAndExponent(rawBitsA, biasedExponentA - 1023);
            final ExpandedDouble edB = ExpandedDouble.fromRawBitsAndExponent(rawBitsB, biasedExponentB - 1023);
            final NormalisedDecimal ndA = edA.normaliseBaseTen().roundUnits();
            final NormalisedDecimal ndB = edB.normaliseBaseTen().roundUnits();
            cmp = ndA.compareNormalised(ndB);
            if (aIsNegative) {
                return -cmp;
            }
            return cmp;
        }
    }
    
    private static int compareSubnormalNumbers(final long fracA, final long fracB, final boolean isNegative) {
        final int cmp = (fracA > fracB) ? 1 : ((fracA < fracB) ? -1 : 0);
        return isNegative ? (-cmp) : cmp;
    }
    
    private static int compareAcrossSubnormalThreshold(final long normalRawBitsA, final long subnormalRawBitsB, final boolean isNegative) {
        final long fracB = subnormalRawBitsB & 0xFFFFFFFFFFFFFL;
        if (fracB == 0L) {
            return isNegative ? -1 : 1;
        }
        final long fracA = normalRawBitsA & 0xFFFFFFFFFFFFFL;
        if (fracA > 7L || fracB < 4503599627370490L) {
            return isNegative ? -1 : 1;
        }
        if (fracA == 7L && fracB == 4503599627370490L) {
            return 0;
        }
        return isNegative ? 1 : -1;
    }
    
    private static String toHex(final double a) {
        return "0x" + Long.toHexString(Double.doubleToLongBits(a)).toUpperCase();
    }
}

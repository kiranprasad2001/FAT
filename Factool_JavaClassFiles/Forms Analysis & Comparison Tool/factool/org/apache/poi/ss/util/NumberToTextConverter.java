// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.util;

public final class NumberToTextConverter
{
    private static final long EXCEL_NAN_BITS = -276939487313920L;
    private static final int MAX_TEXT_LEN = 20;
    
    private NumberToTextConverter() {
    }
    
    public static String toText(final double value) {
        return rawDoubleBitsToText(Double.doubleToLongBits(value));
    }
    
    static String rawDoubleBitsToText(final long pRawBits) {
        long rawBits = pRawBits;
        boolean isNegative = rawBits < 0L;
        if (isNegative) {
            rawBits &= Long.MAX_VALUE;
        }
        if (rawBits == 0L) {
            return isNegative ? "-0" : "0";
        }
        final ExpandedDouble ed = new ExpandedDouble(rawBits);
        if (ed.getBinaryExponent() < -1022) {
            return isNegative ? "-0" : "0";
        }
        if (ed.getBinaryExponent() == 1024) {
            if (rawBits == -276939487313920L) {
                return "3.484840871308E+308";
            }
            isNegative = false;
        }
        final NormalisedDecimal nd = ed.normaliseBaseTen();
        final StringBuilder sb = new StringBuilder(21);
        if (isNegative) {
            sb.append('-');
        }
        convertToText(sb, nd);
        return sb.toString();
    }
    
    private static void convertToText(final StringBuilder sb, final NormalisedDecimal pnd) {
        final NormalisedDecimal rnd = pnd.roundUnits();
        int decExponent = rnd.getDecimalExponent();
        String decimalDigits;
        if (Math.abs(decExponent) > 98) {
            decimalDigits = rnd.getSignificantDecimalDigitsLastDigitRounded();
            if (decimalDigits.length() == 16) {
                ++decExponent;
            }
        }
        else {
            decimalDigits = rnd.getSignificantDecimalDigits();
        }
        final int countSigDigits = countSignifantDigits(decimalDigits);
        if (decExponent < 0) {
            formatLessThanOne(sb, decimalDigits, decExponent, countSigDigits);
        }
        else {
            formatGreaterThanOne(sb, decimalDigits, decExponent, countSigDigits);
        }
    }
    
    private static void formatLessThanOne(final StringBuilder sb, final String decimalDigits, final int decExponent, final int countSigDigits) {
        final int nLeadingZeros = -decExponent - 1;
        final int normalLength = 2 + nLeadingZeros + countSigDigits;
        if (needsScientificNotation(normalLength)) {
            sb.append(decimalDigits.charAt(0));
            if (countSigDigits > 1) {
                sb.append('.');
                sb.append(decimalDigits.subSequence(1, countSigDigits));
            }
            sb.append("E-");
            appendExp(sb, -decExponent);
            return;
        }
        sb.append("0.");
        for (int i = nLeadingZeros; i > 0; --i) {
            sb.append('0');
        }
        sb.append(decimalDigits.subSequence(0, countSigDigits));
    }
    
    private static void formatGreaterThanOne(final StringBuilder sb, final String decimalDigits, final int decExponent, final int countSigDigits) {
        if (decExponent > 19) {
            sb.append(decimalDigits.charAt(0));
            if (countSigDigits > 1) {
                sb.append('.');
                sb.append(decimalDigits.subSequence(1, countSigDigits));
            }
            sb.append("E+");
            appendExp(sb, decExponent);
            return;
        }
        final int nFractionalDigits = countSigDigits - decExponent - 1;
        if (nFractionalDigits > 0) {
            sb.append(decimalDigits.subSequence(0, decExponent + 1));
            sb.append('.');
            sb.append(decimalDigits.subSequence(decExponent + 1, countSigDigits));
            return;
        }
        sb.append(decimalDigits.subSequence(0, countSigDigits));
        for (int i = -nFractionalDigits; i > 0; --i) {
            sb.append('0');
        }
    }
    
    private static boolean needsScientificNotation(final int nDigits) {
        return nDigits > 20;
    }
    
    private static int countSignifantDigits(final String sb) {
        int result = sb.length() - 1;
        while (sb.charAt(result) == '0') {
            if (--result < 0) {
                throw new RuntimeException("No non-zero digits found");
            }
        }
        return result + 1;
    }
    
    private static void appendExp(final StringBuilder sb, final int val) {
        if (val < 10) {
            sb.append('0');
            sb.append((char)(48 + val));
            return;
        }
        sb.append(val);
    }
}

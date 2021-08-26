// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.functions;

public class BaseNumberUtils
{
    public static double convertToDecimal(final String value, final int base, final int maxNumberOfPlaces) throws IllegalArgumentException {
        if (value == null || value.length() == 0) {
            return 0.0;
        }
        final long stringLength = value.length();
        if (stringLength > maxNumberOfPlaces) {
            throw new IllegalArgumentException();
        }
        double decimalValue = 0.0;
        long signedDigit = 0L;
        boolean hasSignedDigit = true;
        final char[] arr$;
        final char[] characters = arr$ = value.toCharArray();
        for (final char character : arr$) {
            long digit;
            if ('0' <= character && character <= '9') {
                digit = character - '0';
            }
            else if ('A' <= character && character <= 'Z') {
                digit = 10 + (character - 'A');
            }
            else if ('a' <= character && character <= 'z') {
                digit = 10 + (character - 'a');
            }
            else {
                digit = base;
            }
            if (digit >= base) {
                throw new IllegalArgumentException("character not allowed");
            }
            if (hasSignedDigit) {
                hasSignedDigit = false;
                signedDigit = digit;
            }
            decimalValue = decimalValue * base + digit;
        }
        final boolean isNegative = !hasSignedDigit && stringLength == maxNumberOfPlaces && signedDigit >= base / 2;
        if (isNegative) {
            decimalValue = getTwoComplement(base, maxNumberOfPlaces, decimalValue);
            decimalValue *= -1.0;
        }
        return decimalValue;
    }
    
    private static double getTwoComplement(final double base, final double maxNumberOfPlaces, final double decimalValue) {
        return Math.pow(base, maxNumberOfPlaces) - decimalValue;
    }
}

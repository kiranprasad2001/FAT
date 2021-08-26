// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.hssf.util;

public final class RKUtil
{
    private RKUtil() {
    }
    
    public static double decodeNumber(final int number) {
        long raw_number = number;
        raw_number >>= 2;
        double rvalue = 0.0;
        if ((number & 0x2) == 0x2) {
            rvalue = (double)raw_number;
        }
        else {
            rvalue = Double.longBitsToDouble(raw_number << 34);
        }
        if ((number & 0x1) == 0x1) {
            rvalue /= 100.0;
        }
        return rvalue;
    }
}

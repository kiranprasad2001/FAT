// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.util;

public class Units
{
    public static final int EMU_PER_PIXEL = 9525;
    public static final int EMU_PER_POINT = 12700;
    
    public static int toEMU(final double points) {
        return (int)Math.round(12700.0 * points);
    }
    
    public static double toPoints(final long emu) {
        return emu / 12700.0;
    }
    
    public static double fixedPointToDecimal(final int fixedPoint) {
        final int i = fixedPoint >> 16;
        final int f = fixedPoint >> 0 & 0xFFFF;
        final double decimal = i + f / 65536.0;
        return decimal;
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.util;

public final class PngUtils
{
    private static final byte[] PNG_FILE_HEADER;
    
    private PngUtils() {
    }
    
    public static boolean matchesPngHeader(final byte[] data, final int offset) {
        if (data == null || data.length - offset < PngUtils.PNG_FILE_HEADER.length) {
            return false;
        }
        for (int i = 0; i < PngUtils.PNG_FILE_HEADER.length; ++i) {
            if (PngUtils.PNG_FILE_HEADER[i] != data[i + offset]) {
                return false;
            }
        }
        return true;
    }
    
    static {
        PNG_FILE_HEADER = new byte[] { -119, 80, 78, 71, 13, 10, 26, 10 };
    }
}

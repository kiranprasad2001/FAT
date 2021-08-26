// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.File;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.io.IOException;
import java.io.OutputStream;

public class HexDump
{
    public static final String EOL;
    private static final char[] _hexcodes;
    private static final int[] _shifts;
    
    private HexDump() {
    }
    
    public static void dump(final byte[] data, final long offset, final OutputStream stream, final int index, final int length) throws IOException, ArrayIndexOutOfBoundsException, IllegalArgumentException {
        if (data.length == 0) {
            stream.write(("No Data" + HexDump.EOL).getBytes());
            stream.flush();
            return;
        }
        if (index < 0 || index >= data.length) {
            throw new ArrayIndexOutOfBoundsException("illegal index: " + index + " into array of length " + data.length);
        }
        if (stream == null) {
            throw new IllegalArgumentException("cannot write to nullstream");
        }
        long display_offset = offset + index;
        final StringBuffer buffer = new StringBuffer(74);
        for (int data_length = Math.min(data.length, index + length), j = index; j < data_length; j += 16) {
            int chars_read = data_length - j;
            if (chars_read > 16) {
                chars_read = 16;
            }
            buffer.append(dump(display_offset)).append(' ');
            for (int k = 0; k < 16; ++k) {
                if (k < chars_read) {
                    buffer.append(dump(data[k + j]));
                }
                else {
                    buffer.append("  ");
                }
                buffer.append(' ');
            }
            for (int k = 0; k < chars_read; ++k) {
                if (data[k + j] >= 32 && data[k + j] < 127) {
                    buffer.append((char)data[k + j]);
                }
                else {
                    buffer.append('.');
                }
            }
            buffer.append(HexDump.EOL);
            stream.write(buffer.toString().getBytes());
            stream.flush();
            buffer.setLength(0);
            display_offset += chars_read;
        }
    }
    
    public static synchronized void dump(final byte[] data, final long offset, final OutputStream stream, final int index) throws IOException, ArrayIndexOutOfBoundsException, IllegalArgumentException {
        dump(data, offset, stream, index, data.length - index);
    }
    
    public static String dump(final byte[] data, final long offset, final int index) {
        if (index < 0 || index >= data.length) {
            throw new ArrayIndexOutOfBoundsException("illegal index: " + index + " into array of length " + data.length);
        }
        long display_offset = offset + index;
        final StringBuffer buffer = new StringBuffer(74);
        for (int j = index; j < data.length; j += 16) {
            int chars_read = data.length - j;
            if (chars_read > 16) {
                chars_read = 16;
            }
            buffer.append(dump(display_offset)).append(' ');
            for (int k = 0; k < 16; ++k) {
                if (k < chars_read) {
                    buffer.append(dump(data[k + j]));
                }
                else {
                    buffer.append("  ");
                }
                buffer.append(' ');
            }
            for (int k = 0; k < chars_read; ++k) {
                if (data[k + j] >= 32 && data[k + j] < 127) {
                    buffer.append((char)data[k + j]);
                }
                else {
                    buffer.append('.');
                }
            }
            buffer.append(HexDump.EOL);
            display_offset += chars_read;
        }
        return buffer.toString();
    }
    
    private static String dump(final long value) {
        final StringBuffer buf = new StringBuffer();
        buf.setLength(0);
        for (int j = 0; j < 8; ++j) {
            buf.append(HexDump._hexcodes[(int)(value >> HexDump._shifts[j + HexDump._shifts.length - 8]) & 0xF]);
        }
        return buf.toString();
    }
    
    private static String dump(final byte value) {
        final StringBuffer buf = new StringBuffer();
        buf.setLength(0);
        for (int j = 0; j < 2; ++j) {
            buf.append(HexDump._hexcodes[value >> HexDump._shifts[j + 6] & 0xF]);
        }
        return buf.toString();
    }
    
    public static String toHex(final byte[] value) {
        final StringBuffer retVal = new StringBuffer();
        retVal.append('[');
        for (int x = 0; x < value.length; ++x) {
            if (x > 0) {
                retVal.append(", ");
            }
            retVal.append(toHex(value[x]));
        }
        retVal.append(']');
        return retVal.toString();
    }
    
    public static String toHex(final short[] value) {
        final StringBuffer retVal = new StringBuffer();
        retVal.append('[');
        for (int x = 0; x < value.length; ++x) {
            if (x > 0) {
                retVal.append(", ");
            }
            retVal.append(toHex(value[x]));
        }
        retVal.append(']');
        return retVal.toString();
    }
    
    public static String toHex(final byte[] value, final int bytesPerLine) {
        final int digits = (int)Math.round(Math.log(value.length) / Math.log(10.0) + 0.5);
        final StringBuffer formatString = new StringBuffer();
        for (int i = 0; i < digits; ++i) {
            formatString.append('0');
        }
        formatString.append(": ");
        final DecimalFormat format = new DecimalFormat(formatString.toString());
        final StringBuffer retVal = new StringBuffer();
        retVal.append(format.format(0L));
        int j = -1;
        for (int x = 0; x < value.length; ++x) {
            if (++j == bytesPerLine) {
                retVal.append('\n');
                retVal.append(format.format(x));
                j = 0;
            }
            else if (x > 0) {
                retVal.append(", ");
            }
            retVal.append(toHex(value[x]));
        }
        return retVal.toString();
    }
    
    public static String toHex(final short value) {
        return toHex(value, 4);
    }
    
    public static String toHex(final byte value) {
        return toHex(value, 2);
    }
    
    public static String toHex(final int value) {
        return toHex(value, 8);
    }
    
    public static String toHex(final long value) {
        return toHex(value, 16);
    }
    
    private static String toHex(final long value, final int digits) {
        final StringBuffer result = new StringBuffer(digits);
        for (int j = 0; j < digits; ++j) {
            result.append(HexDump._hexcodes[(int)(value >> HexDump._shifts[j + (16 - digits)] & 0xFL)]);
        }
        return result.toString();
    }
    
    public static void dump(final InputStream in, final PrintStream out, final int start, final int bytesToDump) throws IOException {
        final ByteArrayOutputStream buf = new ByteArrayOutputStream();
        if (bytesToDump == -1) {
            for (int c = in.read(); c != -1; c = in.read()) {
                buf.write(c);
            }
        }
        else {
            int bytesRemaining = bytesToDump;
            while (bytesRemaining-- > 0) {
                final int c2 = in.read();
                if (c2 == -1) {
                    break;
                }
                buf.write(c2);
            }
        }
        final byte[] data = buf.toByteArray();
        dump(data, 0L, out, start, data.length);
    }
    
    private static char[] toHexChars(final long pValue, final int nBytes) {
        int charPos = 2 + nBytes * 2;
        final char[] result = new char[charPos];
        long value = pValue;
        do {
            result[--charPos] = HexDump._hexcodes[(int)(value & 0xFL)];
            value >>>= 4;
        } while (charPos > 1);
        result[0] = '0';
        result[1] = 'x';
        return result;
    }
    
    public static char[] longToHex(final long value) {
        return toHexChars(value, 8);
    }
    
    public static char[] intToHex(final int value) {
        return toHexChars(value, 4);
    }
    
    public static char[] shortToHex(final int value) {
        return toHexChars(value, 2);
    }
    
    public static char[] byteToHex(final int value) {
        return toHexChars(value, 1);
    }
    
    public static void main(final String[] args) throws Exception {
        final File file = new File(args[0]);
        final InputStream in = new BufferedInputStream(new FileInputStream(file));
        final byte[] b = new byte[(int)file.length()];
        in.read(b);
        System.out.println(dump(b, 0L, 0));
        in.close();
    }
    
    static {
        EOL = System.getProperty("line.separator");
        _hexcodes = "0123456789ABCDEF".toCharArray();
        _shifts = new int[] { 60, 56, 52, 48, 44, 40, 36, 32, 28, 24, 20, 16, 12, 8, 4, 0 };
    }
}

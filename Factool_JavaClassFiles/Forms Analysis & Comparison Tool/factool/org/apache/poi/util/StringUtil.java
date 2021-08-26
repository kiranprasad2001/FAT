// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.util;

import java.util.Iterator;
import java.util.HashMap;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.util.Map;
import java.nio.charset.Charset;

public class StringUtil
{
    private static final Charset ISO_8859_1;
    private static final Charset UTF16LE;
    private static Map<Integer, Integer> msCodepointToUnicode;
    private static final int[] symbolMap_f020;
    private static final int[] symbolMap_f0a0;
    
    private StringUtil() {
    }
    
    public static String getFromUnicodeLE(final byte[] string, final int offset, final int len) throws ArrayIndexOutOfBoundsException, IllegalArgumentException {
        if (offset < 0 || offset >= string.length) {
            throw new ArrayIndexOutOfBoundsException("Illegal offset " + offset + " (String data is of length " + string.length + ")");
        }
        if (len < 0 || (string.length - offset) / 2 < len) {
            throw new IllegalArgumentException("Illegal length " + len);
        }
        return new String(string, offset, len * 2, StringUtil.UTF16LE);
    }
    
    public static String getFromUnicodeLE(final byte[] string) {
        if (string.length == 0) {
            return "";
        }
        return getFromUnicodeLE(string, 0, string.length / 2);
    }
    
    public static byte[] getToUnicodeLE(final String string) {
        return string.getBytes(StringUtil.UTF16LE);
    }
    
    public static String getFromCompressedUnicode(final byte[] string, final int offset, final int len) {
        final int len_to_use = Math.min(len, string.length - offset);
        return new String(string, offset, len_to_use, StringUtil.ISO_8859_1);
    }
    
    public static String readCompressedUnicode(final LittleEndianInput in, final int nChars) {
        final byte[] buf = new byte[nChars];
        in.readFully(buf);
        return new String(buf, StringUtil.ISO_8859_1);
    }
    
    public static String readUnicodeString(final LittleEndianInput in) {
        final int nChars = in.readUShort();
        final byte flag = in.readByte();
        if ((flag & 0x1) == 0x0) {
            return readCompressedUnicode(in, nChars);
        }
        return readUnicodeLE(in, nChars);
    }
    
    public static String readUnicodeString(final LittleEndianInput in, final int nChars) {
        final byte is16Bit = in.readByte();
        if ((is16Bit & 0x1) == 0x0) {
            return readCompressedUnicode(in, nChars);
        }
        return readUnicodeLE(in, nChars);
    }
    
    public static void writeUnicodeString(final LittleEndianOutput out, final String value) {
        final int nChars = value.length();
        out.writeShort(nChars);
        final boolean is16Bit = hasMultibyte(value);
        out.writeByte(is16Bit ? 1 : 0);
        if (is16Bit) {
            putUnicodeLE(value, out);
        }
        else {
            putCompressedUnicode(value, out);
        }
    }
    
    public static void writeUnicodeStringFlagAndData(final LittleEndianOutput out, final String value) {
        final boolean is16Bit = hasMultibyte(value);
        out.writeByte(is16Bit ? 1 : 0);
        if (is16Bit) {
            putUnicodeLE(value, out);
        }
        else {
            putCompressedUnicode(value, out);
        }
    }
    
    public static int getEncodedSize(final String value) {
        int result = 3;
        result += value.length() * (hasMultibyte(value) ? 2 : 1);
        return result;
    }
    
    public static void putCompressedUnicode(final String input, final byte[] output, final int offset) {
        final byte[] bytes = input.getBytes(StringUtil.ISO_8859_1);
        System.arraycopy(bytes, 0, output, offset, bytes.length);
    }
    
    public static void putCompressedUnicode(final String input, final LittleEndianOutput out) {
        final byte[] bytes = input.getBytes(StringUtil.ISO_8859_1);
        out.write(bytes);
    }
    
    public static void putUnicodeLE(final String input, final byte[] output, final int offset) {
        final byte[] bytes = input.getBytes(StringUtil.UTF16LE);
        System.arraycopy(bytes, 0, output, offset, bytes.length);
    }
    
    public static void putUnicodeLE(final String input, final LittleEndianOutput out) {
        final byte[] bytes = input.getBytes(StringUtil.UTF16LE);
        out.write(bytes);
    }
    
    public static String readUnicodeLE(final LittleEndianInput in, final int nChars) {
        final byte[] bytes = new byte[nChars * 2];
        in.readFully(bytes);
        return new String(bytes, StringUtil.UTF16LE);
    }
    
    public static String format(final String message, final Object[] params) {
        int currentParamNumber = 0;
        final StringBuffer formattedMessage = new StringBuffer();
        for (int i = 0; i < message.length(); ++i) {
            if (message.charAt(i) == '%') {
                if (currentParamNumber >= params.length) {
                    formattedMessage.append("?missing data?");
                }
                else if (params[currentParamNumber] instanceof Number && i + 1 < message.length()) {
                    i += matchOptionalFormatting((Number)params[currentParamNumber++], message.substring(i + 1), formattedMessage);
                }
                else {
                    formattedMessage.append(params[currentParamNumber++].toString());
                }
            }
            else if (message.charAt(i) == '\\' && i + 1 < message.length() && message.charAt(i + 1) == '%') {
                formattedMessage.append('%');
                ++i;
            }
            else {
                formattedMessage.append(message.charAt(i));
            }
        }
        return formattedMessage.toString();
    }
    
    private static int matchOptionalFormatting(final Number number, final String formatting, final StringBuffer outputTo) {
        final NumberFormat numberFormat = NumberFormat.getInstance();
        if (0 < formatting.length() && Character.isDigit(formatting.charAt(0))) {
            numberFormat.setMinimumIntegerDigits(Integer.parseInt(formatting.charAt(0) + ""));
            if (2 < formatting.length() && formatting.charAt(1) == '.' && Character.isDigit(formatting.charAt(2))) {
                numberFormat.setMaximumFractionDigits(Integer.parseInt(formatting.charAt(2) + ""));
                numberFormat.format(number, outputTo, new FieldPosition(0));
                return 3;
            }
            numberFormat.format(number, outputTo, new FieldPosition(0));
            return 1;
        }
        else {
            if (0 < formatting.length() && formatting.charAt(0) == '.' && 1 < formatting.length() && Character.isDigit(formatting.charAt(1))) {
                numberFormat.setMaximumFractionDigits(Integer.parseInt(formatting.charAt(1) + ""));
                numberFormat.format(number, outputTo, new FieldPosition(0));
                return 2;
            }
            numberFormat.format(number, outputTo, new FieldPosition(0));
            return 1;
        }
    }
    
    public static String getPreferredEncoding() {
        return StringUtil.ISO_8859_1.name();
    }
    
    public static boolean hasMultibyte(final String value) {
        if (value == null) {
            return false;
        }
        for (int i = 0; i < value.length(); ++i) {
            final char c = value.charAt(i);
            if (c > '\u00ff') {
                return true;
            }
        }
        return false;
    }
    
    public static boolean isUnicodeString(final String value) {
        return !value.equals(new String(value.getBytes(StringUtil.ISO_8859_1), StringUtil.ISO_8859_1));
    }
    
    public static String mapMsCodepointString(final String string) {
        if (string == null || "".equals(string)) {
            return string;
        }
        initMsCodepointMap();
        final StringBuilder sb = new StringBuilder();
        Integer msCodepoint;
        for (int length = string.length(), offset = 0; offset < length; offset += Character.charCount(msCodepoint)) {
            msCodepoint = string.codePointAt(offset);
            final Integer uniCodepoint = StringUtil.msCodepointToUnicode.get(msCodepoint);
            sb.appendCodePoint((uniCodepoint == null) ? msCodepoint : uniCodepoint);
        }
        return sb.toString();
    }
    
    public static synchronized void mapMsCodepoint(final int msCodepoint, final int unicodeCodepoint) {
        initMsCodepointMap();
        StringUtil.msCodepointToUnicode.put(msCodepoint, unicodeCodepoint);
    }
    
    private static synchronized void initMsCodepointMap() {
        if (StringUtil.msCodepointToUnicode != null) {
            return;
        }
        StringUtil.msCodepointToUnicode = new HashMap<Integer, Integer>();
        int i = 61472;
        for (final int ch : StringUtil.symbolMap_f020) {
            StringUtil.msCodepointToUnicode.put(i++, ch);
        }
        i = 61600;
        for (final int ch : StringUtil.symbolMap_f0a0) {
            StringUtil.msCodepointToUnicode.put(i++, ch);
        }
    }
    
    static {
        ISO_8859_1 = Charset.forName("ISO-8859-1");
        UTF16LE = Charset.forName("UTF-16LE");
        symbolMap_f020 = new int[] { 32, 33, 8704, 35, 8707, 37, 38, 8717, 40, 41, 8727, 43, 44, 8722, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 8773, 913, 914, 935, 916, 917, 934, 915, 919, 921, 977, 922, 923, 924, 925, 927, 928, 920, 929, 931, 932, 933, 962, 937, 926, 936, 918, 91, 8765, 93, 8869, 95, 32, 945, 946, 967, 948, 949, 966, 947, 951, 953, 981, 954, 955, 956, 957, 959, 960, 952, 961, 963, 964, 965, 982, 969, 958, 968, 950, 123, 124, 125, 8764, 32 };
        symbolMap_f0a0 = new int[] { 8364, 978, 8242, 8804, 8260, 8734, 402, 9827, 9830, 9829, 9824, 8596, 8591, 8593, 8594, 8595, 176, 177, 8243, 8805, 215, 181, 8706, 8729, 247, 8800, 8801, 8776, 8230, 9168, 9135, 8629, 8501, 8475, 8476, 8472, 8855, 8853, 8709, 8745, 8746, 8835, 8839, 8836, 8834, 8838, 8712, 8713, 8736, 8711, 174, 169, 8482, 8719, 8730, 8901, 172, 8743, 8744, 8660, 8656, 8657, 8658, 8659, 9674, 9001, 174, 169, 8482, 8721, 9115, 9116, 9117, 9121, 9122, 9123, 9127, 9128, 9129, 9130, 32, 9002, 8747, 8992, 9134, 8993, 9118, 9119, 9120, 9124, 9125, 9126, 9131, 9132, 9133, 32 };
    }
    
    public static class StringsIterator implements Iterator<String>
    {
        private String[] strings;
        private int position;
        
        public StringsIterator(final String[] strings) {
            this.position = 0;
            if (strings != null) {
                this.strings = strings;
            }
            else {
                this.strings = new String[0];
            }
        }
        
        @Override
        public boolean hasNext() {
            return this.position < this.strings.length;
        }
        
        @Override
        public String next() {
            final int ourPos = this.position++;
            if (ourPos >= this.strings.length) {
                throw new ArrayIndexOutOfBoundsException(ourPos);
            }
            return this.strings[ourPos];
        }
        
        @Override
        public void remove() {
        }
    }
}

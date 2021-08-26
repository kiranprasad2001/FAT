// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.constant;

import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.StringUtil;
import org.apache.poi.util.LittleEndianInput;

public final class ConstantValueParser
{
    private static final int TYPE_EMPTY = 0;
    private static final int TYPE_NUMBER = 1;
    private static final int TYPE_STRING = 2;
    private static final int TYPE_BOOLEAN = 4;
    private static final int TYPE_ERROR_CODE = 16;
    private static final int TRUE_ENCODING = 1;
    private static final int FALSE_ENCODING = 0;
    private static final Object EMPTY_REPRESENTATION;
    
    private ConstantValueParser() {
    }
    
    public static Object[] parse(final LittleEndianInput in, final int nValues) {
        final Object[] result = new Object[nValues];
        for (int i = 0; i < result.length; ++i) {
            result[i] = readAConstantValue(in);
        }
        return result;
    }
    
    private static Object readAConstantValue(final LittleEndianInput in) {
        final byte grbit = in.readByte();
        switch (grbit) {
            case 0: {
                in.readLong();
                return ConstantValueParser.EMPTY_REPRESENTATION;
            }
            case 1: {
                return new Double(in.readDouble());
            }
            case 2: {
                return StringUtil.readUnicodeString(in);
            }
            case 4: {
                return readBoolean(in);
            }
            case 16: {
                final int errCode = in.readUShort();
                in.readUShort();
                in.readInt();
                return ErrorConstant.valueOf(errCode);
            }
            default: {
                throw new RuntimeException("Unknown grbit value (" + grbit + ")");
            }
        }
    }
    
    private static Object readBoolean(final LittleEndianInput in) {
        final byte val = (byte)in.readLong();
        switch (val) {
            case 0: {
                return Boolean.FALSE;
            }
            case 1: {
                return Boolean.TRUE;
            }
            default: {
                throw new RuntimeException("unexpected boolean encoding (" + val + ")");
            }
        }
    }
    
    public static int getEncodedSize(final Object[] values) {
        int result = values.length * 1;
        for (int i = 0; i < values.length; ++i) {
            result += getEncodedSize(values[i]);
        }
        return result;
    }
    
    private static int getEncodedSize(final Object object) {
        if (object == ConstantValueParser.EMPTY_REPRESENTATION) {
            return 8;
        }
        final Class<?> cls = object.getClass();
        if (cls == Boolean.class || cls == Double.class || cls == ErrorConstant.class) {
            return 8;
        }
        final String strVal = (String)object;
        return StringUtil.getEncodedSize(strVal);
    }
    
    public static void encode(final LittleEndianOutput out, final Object[] values) {
        for (int i = 0; i < values.length; ++i) {
            encodeSingleValue(out, values[i]);
        }
    }
    
    private static void encodeSingleValue(final LittleEndianOutput out, final Object value) {
        if (value == ConstantValueParser.EMPTY_REPRESENTATION) {
            out.writeByte(0);
            out.writeLong(0L);
            return;
        }
        if (value instanceof Boolean) {
            final Boolean bVal = (Boolean)value;
            out.writeByte(4);
            final long longVal = ((boolean)bVal) ? 1 : 0;
            out.writeLong(longVal);
            return;
        }
        if (value instanceof Double) {
            final Double dVal = (Double)value;
            out.writeByte(1);
            out.writeDouble(dVal);
            return;
        }
        if (value instanceof String) {
            final String val = (String)value;
            out.writeByte(2);
            StringUtil.writeUnicodeString(out, val);
            return;
        }
        if (value instanceof ErrorConstant) {
            final ErrorConstant ecVal = (ErrorConstant)value;
            out.writeByte(16);
            final long longVal = ecVal.getErrorCode();
            out.writeLong(longVal);
            return;
        }
        throw new IllegalStateException("Unexpected value type (" + value.getClass().getName() + "'");
    }
    
    static {
        EMPTY_REPRESENTATION = null;
    }
}

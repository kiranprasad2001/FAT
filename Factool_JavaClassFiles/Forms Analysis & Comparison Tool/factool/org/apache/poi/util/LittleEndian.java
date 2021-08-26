// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.util;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;

public class LittleEndian implements LittleEndianConsts
{
    public static byte[] getByteArray(final byte[] data, final int offset, final int size) {
        final byte[] copy = new byte[size];
        System.arraycopy(data, offset, copy, 0, size);
        return copy;
    }
    
    public static double getDouble(final byte[] data) {
        return Double.longBitsToDouble(getLong(data, 0));
    }
    
    public static double getDouble(final byte[] data, final int offset) {
        return Double.longBitsToDouble(getLong(data, offset));
    }
    
    public static float getFloat(final byte[] data) {
        return getFloat(data, 0);
    }
    
    public static float getFloat(final byte[] data, final int offset) {
        return Float.intBitsToFloat(getInt(data, offset));
    }
    
    public static int getInt(final byte[] data) {
        return getInt(data, 0);
    }
    
    public static int getInt(final byte[] data, final int offset) {
        int i = offset;
        final int b0 = data[i++] & 0xFF;
        final int b2 = data[i++] & 0xFF;
        final int b3 = data[i++] & 0xFF;
        final int b4 = data[i++] & 0xFF;
        return (b4 << 24) + (b3 << 16) + (b2 << 8) + (b0 << 0);
    }
    
    public static long getLong(final byte[] data) {
        return getLong(data, 0);
    }
    
    public static long getLong(final byte[] data, final int offset) {
        long result = 0xFF & data[offset + 7];
        for (int j = offset + 8 - 1; j >= offset; --j) {
            result <<= 8;
            result |= (0xFF & data[j]);
        }
        return result;
    }
    
    public static short getShort(final byte[] data) {
        return getShort(data, 0);
    }
    
    public static short getShort(final byte[] data, final int offset) {
        final int b0 = data[offset] & 0xFF;
        final int b2 = data[offset + 1] & 0xFF;
        return (short)((b2 << 8) + (b0 << 0));
    }
    
    public static short[] getShortArray(final byte[] data, final int offset, final int size) {
        final short[] result = new short[size / 2];
        for (int i = 0; i < result.length; ++i) {
            result[i] = getShort(data, offset + i * 2);
        }
        return result;
    }
    
    public static short getUByte(final byte[] data) {
        return (short)(data[0] & 0xFF);
    }
    
    public static short getUByte(final byte[] data, final int offset) {
        return (short)(data[offset] & 0xFF);
    }
    
    public static long getUInt(final byte[] data) {
        return getUInt(data, 0);
    }
    
    public static long getUInt(final byte[] data, final int offset) {
        final long retNum = getInt(data, offset);
        return retNum & 0xFFFFFFFFL;
    }
    
    @Deprecated
    public static int getUnsignedByte(final byte[] data, final int offset) {
        return data[offset] & 0xFF;
    }
    
    public static int getUShort(final byte[] data) {
        return getUShort(data, 0);
    }
    
    public static int getUShort(final byte[] data, final int offset) {
        final int b0 = data[offset] & 0xFF;
        final int b2 = data[offset + 1] & 0xFF;
        return (b2 << 8) + (b0 << 0);
    }
    
    public static void putByte(final byte[] data, final int offset, final int value) {
        data[offset] = (byte)value;
    }
    
    public static void putDouble(final byte[] data, final int offset, final double value) {
        putLong(data, offset, Double.doubleToLongBits(value));
    }
    
    public static void putDouble(final double value, final OutputStream outputStream) throws IOException {
        putLong(Double.doubleToLongBits(value), outputStream);
    }
    
    public static void putFloat(final byte[] data, final int offset, final float value) {
        putInt(data, offset, Float.floatToIntBits(value));
    }
    
    public static void putFloat(final float value, final OutputStream outputStream) throws IOException {
        putInt(Float.floatToIntBits(value), outputStream);
    }
    
    @Deprecated
    public static void putInt(final byte[] data, final int value) {
        putInt(data, 0, value);
    }
    
    public static void putInt(final byte[] data, final int offset, final int value) {
        int i = offset;
        data[i++] = (byte)(value >>> 0 & 0xFF);
        data[i++] = (byte)(value >>> 8 & 0xFF);
        data[i++] = (byte)(value >>> 16 & 0xFF);
        data[i++] = (byte)(value >>> 24 & 0xFF);
    }
    
    public static void putInt(final int value, final OutputStream outputStream) throws IOException {
        outputStream.write((byte)(value >>> 0 & 0xFF));
        outputStream.write((byte)(value >>> 8 & 0xFF));
        outputStream.write((byte)(value >>> 16 & 0xFF));
        outputStream.write((byte)(value >>> 24 & 0xFF));
    }
    
    public static void putLong(final byte[] data, final int offset, final long value) {
        data[offset + 0] = (byte)(value >>> 0 & 0xFFL);
        data[offset + 1] = (byte)(value >>> 8 & 0xFFL);
        data[offset + 2] = (byte)(value >>> 16 & 0xFFL);
        data[offset + 3] = (byte)(value >>> 24 & 0xFFL);
        data[offset + 4] = (byte)(value >>> 32 & 0xFFL);
        data[offset + 5] = (byte)(value >>> 40 & 0xFFL);
        data[offset + 6] = (byte)(value >>> 48 & 0xFFL);
        data[offset + 7] = (byte)(value >>> 56 & 0xFFL);
    }
    
    public static void putLong(final long value, final OutputStream outputStream) throws IOException {
        outputStream.write((byte)(value >>> 0 & 0xFFL));
        outputStream.write((byte)(value >>> 8 & 0xFFL));
        outputStream.write((byte)(value >>> 16 & 0xFFL));
        outputStream.write((byte)(value >>> 24 & 0xFFL));
        outputStream.write((byte)(value >>> 32 & 0xFFL));
        outputStream.write((byte)(value >>> 40 & 0xFFL));
        outputStream.write((byte)(value >>> 48 & 0xFFL));
        outputStream.write((byte)(value >>> 56 & 0xFFL));
    }
    
    public static void putShort(final byte[] data, final int offset, final short value) {
        int i = offset;
        data[i++] = (byte)(value >>> 0 & 0xFF);
        data[i++] = (byte)(value >>> 8 & 0xFF);
    }
    
    @Deprecated
    public static void putShort(final byte[] data, final short value) {
        putShort(data, 0, value);
    }
    
    public static void putShort(final OutputStream outputStream, final short value) throws IOException {
        outputStream.write((byte)(value >>> 0 & 0xFF));
        outputStream.write((byte)(value >>> 8 & 0xFF));
    }
    
    public static void putShortArray(final byte[] data, final int startOffset, final short[] value) {
        int offset = startOffset;
        for (final short s : value) {
            putShort(data, offset, s);
            offset += 2;
        }
    }
    
    public static void putUByte(final byte[] data, final int offset, final short value) {
        data[offset] = (byte)(value & 0xFF);
    }
    
    public static void putUInt(final byte[] data, final int offset, final long value) {
        int i = offset;
        data[i++] = (byte)(value >>> 0 & 0xFFL);
        data[i++] = (byte)(value >>> 8 & 0xFFL);
        data[i++] = (byte)(value >>> 16 & 0xFFL);
        data[i++] = (byte)(value >>> 24 & 0xFFL);
    }
    
    @Deprecated
    public static void putUInt(final byte[] data, final long value) {
        putUInt(data, 0, value);
    }
    
    public static void putUInt(final long value, final OutputStream outputStream) throws IOException {
        outputStream.write((byte)(value >>> 0 & 0xFFL));
        outputStream.write((byte)(value >>> 8 & 0xFFL));
        outputStream.write((byte)(value >>> 16 & 0xFFL));
        outputStream.write((byte)(value >>> 24 & 0xFFL));
    }
    
    public static void putUShort(final byte[] data, final int offset, final int value) {
        int i = offset;
        data[i++] = (byte)(value >>> 0 & 0xFF);
        data[i++] = (byte)(value >>> 8 & 0xFF);
    }
    
    public static void putUShort(final int value, final OutputStream outputStream) throws IOException {
        outputStream.write((byte)(value >>> 0 & 0xFF));
        outputStream.write((byte)(value >>> 8 & 0xFF));
    }
    
    public static int readInt(final InputStream stream) throws IOException, BufferUnderrunException {
        final int ch1 = stream.read();
        final int ch2 = stream.read();
        final int ch3 = stream.read();
        final int ch4 = stream.read();
        if ((ch1 | ch2 | ch3 | ch4) < 0) {
            throw new BufferUnderrunException();
        }
        return (ch4 << 24) + (ch3 << 16) + (ch2 << 8) + (ch1 << 0);
    }
    
    public static long readUInt(final InputStream stream) throws IOException, BufferUnderrunException {
        final long retNum = readInt(stream);
        return retNum & 0xFFFFFFFFL;
    }
    
    public static long readLong(final InputStream stream) throws IOException, BufferUnderrunException {
        final int ch1 = stream.read();
        final int ch2 = stream.read();
        final int ch3 = stream.read();
        final int ch4 = stream.read();
        final int ch5 = stream.read();
        final int ch6 = stream.read();
        final int ch7 = stream.read();
        final int ch8 = stream.read();
        if ((ch1 | ch2 | ch3 | ch4 | ch5 | ch6 | ch7 | ch8) < 0) {
            throw new BufferUnderrunException();
        }
        return ((long)ch8 << 56) + ((long)ch7 << 48) + ((long)ch6 << 40) + ((long)ch5 << 32) + ((long)ch4 << 24) + (ch3 << 16) + (ch2 << 8) + (ch1 << 0);
    }
    
    public static short readShort(final InputStream stream) throws IOException, BufferUnderrunException {
        return (short)readUShort(stream);
    }
    
    public static int readUShort(final InputStream stream) throws IOException, BufferUnderrunException {
        final int ch1 = stream.read();
        final int ch2 = stream.read();
        if ((ch1 | ch2) < 0) {
            throw new BufferUnderrunException();
        }
        return (ch2 << 8) + (ch1 << 0);
    }
    
    public static int ubyteToInt(final byte b) {
        return b & 0xFF;
    }
    
    private LittleEndian() {
    }
    
    public static final class BufferUnderrunException extends IOException
    {
        private static final long serialVersionUID = 8736973884877006145L;
        
        BufferUnderrunException() {
            super("buffer underrun");
        }
    }
}

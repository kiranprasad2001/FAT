// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.util;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.io.InputStream;

public class ByteField implements FixedField
{
    private static final byte _default_value = 0;
    private byte _value;
    private final int _offset;
    
    public ByteField(final int offset) throws ArrayIndexOutOfBoundsException {
        this(offset, (byte)0);
    }
    
    public ByteField(final int offset, final byte value) throws ArrayIndexOutOfBoundsException {
        if (offset < 0) {
            throw new ArrayIndexOutOfBoundsException("offset cannot be negative");
        }
        this._offset = offset;
        this.set(value);
    }
    
    public ByteField(final int offset, final byte[] data) throws ArrayIndexOutOfBoundsException {
        this(offset);
        this.readFromBytes(data);
    }
    
    public ByteField(final int offset, final byte value, final byte[] data) throws ArrayIndexOutOfBoundsException {
        this(offset, value);
        this.writeToBytes(data);
    }
    
    public byte get() {
        return this._value;
    }
    
    public void set(final byte value) {
        this._value = value;
    }
    
    public void set(final byte value, final byte[] data) throws ArrayIndexOutOfBoundsException {
        this.set(value);
        this.writeToBytes(data);
    }
    
    @Override
    public void readFromBytes(final byte[] data) throws ArrayIndexOutOfBoundsException {
        this._value = data[this._offset];
    }
    
    @Override
    public void readFromStream(final InputStream stream) throws IOException, LittleEndian.BufferUnderrunException {
        final int ib = stream.read();
        if (ib < 0) {
            throw new BufferUnderflowException();
        }
        this._value = (byte)ib;
    }
    
    @Override
    public void writeToBytes(final byte[] data) throws ArrayIndexOutOfBoundsException {
        data[this._offset] = this._value;
    }
    
    @Override
    public String toString() {
        return String.valueOf(this._value);
    }
}

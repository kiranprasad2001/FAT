// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.util;

import java.io.IOException;
import java.io.InputStream;

public class ShortField implements FixedField
{
    private short _value;
    private final int _offset;
    
    public ShortField(final int offset) throws ArrayIndexOutOfBoundsException {
        if (offset < 0) {
            throw new ArrayIndexOutOfBoundsException("Illegal offset: " + offset);
        }
        this._offset = offset;
    }
    
    public ShortField(final int offset, final short value) throws ArrayIndexOutOfBoundsException {
        this(offset);
        this.set(value);
    }
    
    public ShortField(final int offset, final byte[] data) throws ArrayIndexOutOfBoundsException {
        this(offset);
        this.readFromBytes(data);
    }
    
    public ShortField(final int offset, final short value, final byte[] data) throws ArrayIndexOutOfBoundsException {
        this(offset);
        this.set(value, data);
    }
    
    public short get() {
        return this._value;
    }
    
    public void set(final short value) {
        this._value = value;
    }
    
    public void set(final short value, final byte[] data) throws ArrayIndexOutOfBoundsException {
        this._value = value;
        this.writeToBytes(data);
    }
    
    @Override
    public void readFromBytes(final byte[] data) throws ArrayIndexOutOfBoundsException {
        this._value = LittleEndian.getShort(data, this._offset);
    }
    
    @Override
    public void readFromStream(final InputStream stream) throws IOException, LittleEndian.BufferUnderrunException {
        this._value = LittleEndian.readShort(stream);
    }
    
    @Override
    public void writeToBytes(final byte[] data) throws ArrayIndexOutOfBoundsException {
        LittleEndian.putShort(data, this._offset, this._value);
    }
    
    @Override
    public String toString() {
        return String.valueOf(this._value);
    }
}

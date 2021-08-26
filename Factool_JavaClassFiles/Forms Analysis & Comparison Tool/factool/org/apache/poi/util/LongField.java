// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.util;

import java.io.IOException;
import java.io.InputStream;

public class LongField implements FixedField
{
    private long _value;
    private final int _offset;
    
    public LongField(final int offset) throws ArrayIndexOutOfBoundsException {
        if (offset < 0) {
            throw new ArrayIndexOutOfBoundsException("Illegal offset: " + offset);
        }
        this._offset = offset;
    }
    
    public LongField(final int offset, final long value) throws ArrayIndexOutOfBoundsException {
        this(offset);
        this.set(value);
    }
    
    public LongField(final int offset, final byte[] data) throws ArrayIndexOutOfBoundsException {
        this(offset);
        this.readFromBytes(data);
    }
    
    public LongField(final int offset, final long value, final byte[] data) throws ArrayIndexOutOfBoundsException {
        this(offset);
        this.set(value, data);
    }
    
    public long get() {
        return this._value;
    }
    
    public void set(final long value) {
        this._value = value;
    }
    
    public void set(final long value, final byte[] data) throws ArrayIndexOutOfBoundsException {
        this._value = value;
        this.writeToBytes(data);
    }
    
    @Override
    public void readFromBytes(final byte[] data) throws ArrayIndexOutOfBoundsException {
        this._value = LittleEndian.getLong(data, this._offset);
    }
    
    @Override
    public void readFromStream(final InputStream stream) throws IOException, LittleEndian.BufferUnderrunException {
        this._value = LittleEndian.readLong(stream);
    }
    
    @Override
    public void writeToBytes(final byte[] data) throws ArrayIndexOutOfBoundsException {
        LittleEndian.putLong(data, this._offset, this._value);
    }
    
    @Override
    public String toString() {
        return String.valueOf(this._value);
    }
}

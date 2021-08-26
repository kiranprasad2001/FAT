// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.util;

import java.io.IOException;
import java.io.InputStream;

public class IntegerField implements FixedField
{
    private int _value;
    private final int _offset;
    
    public IntegerField(final int offset) throws ArrayIndexOutOfBoundsException {
        if (offset < 0) {
            throw new ArrayIndexOutOfBoundsException("negative offset");
        }
        this._offset = offset;
    }
    
    public IntegerField(final int offset, final int value) throws ArrayIndexOutOfBoundsException {
        this(offset);
        this.set(value);
    }
    
    public IntegerField(final int offset, final byte[] data) throws ArrayIndexOutOfBoundsException {
        this(offset);
        this.readFromBytes(data);
    }
    
    public IntegerField(final int offset, final int value, final byte[] data) throws ArrayIndexOutOfBoundsException {
        this(offset);
        this.set(value, data);
    }
    
    public int get() {
        return this._value;
    }
    
    public void set(final int value) {
        this._value = value;
    }
    
    public void set(final int value, final byte[] data) throws ArrayIndexOutOfBoundsException {
        this._value = value;
        this.writeToBytes(data);
    }
    
    @Override
    public void readFromBytes(final byte[] data) throws ArrayIndexOutOfBoundsException {
        this._value = LittleEndian.getInt(data, this._offset);
    }
    
    @Override
    public void readFromStream(final InputStream stream) throws IOException, LittleEndian.BufferUnderrunException {
        this._value = LittleEndian.readInt(stream);
    }
    
    @Override
    public void writeToBytes(final byte[] data) throws ArrayIndexOutOfBoundsException {
        LittleEndian.putInt(data, this._offset, this._value);
    }
    
    @Override
    public String toString() {
        return String.valueOf(this._value);
    }
}

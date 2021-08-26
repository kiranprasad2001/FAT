// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.poifs.property;

import java.util.Collections;
import java.util.Iterator;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import org.apache.poi.hpsf.ClassID;
import org.apache.poi.util.IntegerField;
import org.apache.poi.util.ByteField;
import org.apache.poi.util.ShortField;
import org.apache.poi.poifs.dev.POIFSViewable;

public abstract class Property implements Child, POIFSViewable
{
    private static final byte _default_fill = 0;
    private static final int _name_size_offset = 64;
    private static final int _max_name_length = 31;
    protected static final int _NO_INDEX = -1;
    private static final int _node_color_offset = 67;
    private static final int _previous_property_offset = 68;
    private static final int _next_property_offset = 72;
    private static final int _child_property_offset = 76;
    private static final int _storage_clsid_offset = 80;
    private static final int _user_flags_offset = 96;
    private static final int _seconds_1_offset = 100;
    private static final int _days_1_offset = 104;
    private static final int _seconds_2_offset = 108;
    private static final int _days_2_offset = 112;
    private static final int _start_block_offset = 116;
    private static final int _size_offset = 120;
    protected static final byte _NODE_BLACK = 1;
    protected static final byte _NODE_RED = 0;
    private static final int _big_block_minimum_bytes = 4096;
    private String _name;
    private ShortField _name_size;
    private ByteField _property_type;
    private ByteField _node_color;
    private IntegerField _previous_property;
    private IntegerField _next_property;
    private IntegerField _child_property;
    private ClassID _storage_clsid;
    private IntegerField _user_flags;
    private IntegerField _seconds_1;
    private IntegerField _days_1;
    private IntegerField _seconds_2;
    private IntegerField _days_2;
    private IntegerField _start_block;
    private IntegerField _size;
    private byte[] _raw_data;
    private int _index;
    private Child _next_child;
    private Child _previous_child;
    
    protected Property() {
        Arrays.fill(this._raw_data = new byte[128], (byte)0);
        this._name_size = new ShortField(64);
        this._property_type = new ByteField(66);
        this._node_color = new ByteField(67);
        this._previous_property = new IntegerField(68, -1, this._raw_data);
        this._next_property = new IntegerField(72, -1, this._raw_data);
        this._child_property = new IntegerField(76, -1, this._raw_data);
        this._storage_clsid = new ClassID(this._raw_data, 80);
        this._user_flags = new IntegerField(96, 0, this._raw_data);
        this._seconds_1 = new IntegerField(100, 0, this._raw_data);
        this._days_1 = new IntegerField(104, 0, this._raw_data);
        this._seconds_2 = new IntegerField(108, 0, this._raw_data);
        this._days_2 = new IntegerField(112, 0, this._raw_data);
        this._start_block = new IntegerField(116);
        this._size = new IntegerField(120, 0, this._raw_data);
        this._index = -1;
        this.setName("");
        this.setNextChild(null);
        this.setPreviousChild(null);
    }
    
    protected Property(final int index, final byte[] array, final int offset) {
        System.arraycopy(array, offset, this._raw_data = new byte[128], 0, 128);
        this._name_size = new ShortField(64, this._raw_data);
        this._property_type = new ByteField(66, this._raw_data);
        this._node_color = new ByteField(67, this._raw_data);
        this._previous_property = new IntegerField(68, this._raw_data);
        this._next_property = new IntegerField(72, this._raw_data);
        this._child_property = new IntegerField(76, this._raw_data);
        this._storage_clsid = new ClassID(this._raw_data, 80);
        this._user_flags = new IntegerField(96, 0, this._raw_data);
        this._seconds_1 = new IntegerField(100, this._raw_data);
        this._days_1 = new IntegerField(104, this._raw_data);
        this._seconds_2 = new IntegerField(108, this._raw_data);
        this._days_2 = new IntegerField(112, this._raw_data);
        this._start_block = new IntegerField(116, this._raw_data);
        this._size = new IntegerField(120, this._raw_data);
        this._index = index;
        final int name_length = this._name_size.get() / 2 - 1;
        if (name_length < 1) {
            this._name = "";
        }
        else {
            final char[] char_array = new char[name_length];
            int name_offset = 0;
            for (int j = 0; j < name_length; ++j) {
                char_array[j] = (char)new ShortField(name_offset, this._raw_data).get();
                name_offset += 2;
            }
            this._name = new String(char_array, 0, name_length);
        }
        this._next_child = null;
        this._previous_child = null;
    }
    
    public void writeData(final OutputStream stream) throws IOException {
        stream.write(this._raw_data);
    }
    
    public void setStartBlock(final int startBlock) {
        this._start_block.set(startBlock, this._raw_data);
    }
    
    public int getStartBlock() {
        return this._start_block.get();
    }
    
    public int getSize() {
        return this._size.get();
    }
    
    public boolean shouldUseSmallBlocks() {
        return isSmall(this._size.get());
    }
    
    public static boolean isSmall(final int length) {
        return length < 4096;
    }
    
    public String getName() {
        return this._name;
    }
    
    public abstract boolean isDirectory();
    
    public ClassID getStorageClsid() {
        return this._storage_clsid;
    }
    
    protected void setName(final String name) {
        final char[] char_array = name.toCharArray();
        final int limit = Math.min(char_array.length, 31);
        this._name = new String(char_array, 0, limit);
        short offset = 0;
        int j;
        for (j = 0; j < limit; ++j) {
            new ShortField(offset, (short)char_array[j], this._raw_data);
            offset += 2;
        }
        while (j < 32) {
            new ShortField(offset, (short)0, this._raw_data);
            offset += 2;
            ++j;
        }
        this._name_size.set((short)((limit + 1) * 2), this._raw_data);
    }
    
    public void setStorageClsid(final ClassID clsidStorage) {
        this._storage_clsid = clsidStorage;
        if (clsidStorage == null) {
            Arrays.fill(this._raw_data, 80, 96, (byte)0);
        }
        else {
            clsidStorage.write(this._raw_data, 80);
        }
    }
    
    protected void setPropertyType(final byte propertyType) {
        this._property_type.set(propertyType, this._raw_data);
    }
    
    protected void setNodeColor(final byte nodeColor) {
        this._node_color.set(nodeColor, this._raw_data);
    }
    
    protected void setChildProperty(final int child) {
        this._child_property.set(child, this._raw_data);
    }
    
    protected int getChildIndex() {
        return this._child_property.get();
    }
    
    protected void setSize(final int size) {
        this._size.set(size, this._raw_data);
    }
    
    protected void setIndex(final int index) {
        this._index = index;
    }
    
    protected int getIndex() {
        return this._index;
    }
    
    protected abstract void preWrite();
    
    int getNextChildIndex() {
        return this._next_property.get();
    }
    
    int getPreviousChildIndex() {
        return this._previous_property.get();
    }
    
    static boolean isValidIndex(final int index) {
        return index != -1;
    }
    
    @Override
    public Child getNextChild() {
        return this._next_child;
    }
    
    @Override
    public Child getPreviousChild() {
        return this._previous_child;
    }
    
    @Override
    public void setNextChild(final Child child) {
        this._next_child = child;
        this._next_property.set((child == null) ? -1 : ((Property)child).getIndex(), this._raw_data);
    }
    
    @Override
    public void setPreviousChild(final Child child) {
        this._previous_child = child;
        this._previous_property.set((child == null) ? -1 : ((Property)child).getIndex(), this._raw_data);
    }
    
    @Override
    public Object[] getViewableArray() {
        final Object[] results = { "Name          = \"" + this.getName() + "\"", "Property Type = " + this._property_type.get(), "Node Color    = " + this._node_color.get(), null, null };
        long time = this._days_1.get();
        time <<= 32;
        time += ((long)this._seconds_1.get() & 0xFFFFL);
        results[3] = "Time 1        = " + time;
        time = this._days_2.get();
        time <<= 32;
        time += ((long)this._seconds_2.get() & 0xFFFFL);
        results[4] = "Time 2        = " + time;
        return results;
    }
    
    @Override
    public Iterator<Object> getViewableIterator() {
        return Collections.emptyList().iterator();
    }
    
    @Override
    public boolean preferArray() {
        return true;
    }
    
    @Override
    public String getShortDescription() {
        final StringBuffer buffer = new StringBuffer();
        buffer.append("Property: \"").append(this.getName()).append("\"");
        return buffer.toString();
    }
}

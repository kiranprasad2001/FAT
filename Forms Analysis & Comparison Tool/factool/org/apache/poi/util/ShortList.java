// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.util;

public class ShortList
{
    private short[] _array;
    private int _limit;
    private static final int _default_size = 128;
    
    public ShortList() {
        this(128);
    }
    
    public ShortList(final ShortList list) {
        this(list._array.length);
        System.arraycopy(list._array, 0, this._array, 0, this._array.length);
        this._limit = list._limit;
    }
    
    public ShortList(final int initialCapacity) {
        this._array = new short[initialCapacity];
        this._limit = 0;
    }
    
    public void add(final int index, final short value) {
        if (index > this._limit) {
            throw new IndexOutOfBoundsException();
        }
        if (index == this._limit) {
            this.add(value);
        }
        else {
            if (this._limit == this._array.length) {
                this.growArray(this._limit * 2);
            }
            System.arraycopy(this._array, index, this._array, index + 1, this._limit - index);
            this._array[index] = value;
            ++this._limit;
        }
    }
    
    public boolean add(final short value) {
        if (this._limit == this._array.length) {
            this.growArray(this._limit * 2);
        }
        this._array[this._limit++] = value;
        return true;
    }
    
    public boolean addAll(final ShortList c) {
        if (c._limit != 0) {
            if (this._limit + c._limit > this._array.length) {
                this.growArray(this._limit + c._limit);
            }
            System.arraycopy(c._array, 0, this._array, this._limit, c._limit);
            this._limit += c._limit;
        }
        return true;
    }
    
    public boolean addAll(final int index, final ShortList c) {
        if (index > this._limit) {
            throw new IndexOutOfBoundsException();
        }
        if (c._limit != 0) {
            if (this._limit + c._limit > this._array.length) {
                this.growArray(this._limit + c._limit);
            }
            System.arraycopy(this._array, index, this._array, index + c._limit, this._limit - index);
            System.arraycopy(c._array, 0, this._array, index, c._limit);
            this._limit += c._limit;
        }
        return true;
    }
    
    public void clear() {
        this._limit = 0;
    }
    
    public boolean contains(final short o) {
        boolean rval = false;
        for (int j = 0; !rval && j < this._limit; ++j) {
            if (this._array[j] == o) {
                rval = true;
            }
        }
        return rval;
    }
    
    public boolean containsAll(final ShortList c) {
        boolean rval = true;
        if (this != c) {
            for (int j = 0; rval && j < c._limit; ++j) {
                if (!this.contains(c._array[j])) {
                    rval = false;
                }
            }
        }
        return rval;
    }
    
    @Override
    public boolean equals(final Object o) {
        boolean rval = this == o;
        if (!rval && o != null && o.getClass() == this.getClass()) {
            final ShortList other = (ShortList)o;
            if (other._limit == this._limit) {
                rval = true;
                for (int j = 0; rval && j < this._limit; rval = (this._array[j] == other._array[j]), ++j) {}
            }
        }
        return rval;
    }
    
    public short get(final int index) {
        if (index >= this._limit) {
            throw new IndexOutOfBoundsException();
        }
        return this._array[index];
    }
    
    @Override
    public int hashCode() {
        int hash = 0;
        for (int j = 0; j < this._limit; ++j) {
            hash = 31 * hash + this._array[j];
        }
        return hash;
    }
    
    public int indexOf(final short o) {
        int rval;
        for (rval = 0; rval < this._limit && o != this._array[rval]; ++rval) {}
        if (rval == this._limit) {
            rval = -1;
        }
        return rval;
    }
    
    public boolean isEmpty() {
        return this._limit == 0;
    }
    
    public int lastIndexOf(final short o) {
        int rval;
        for (rval = this._limit - 1; rval >= 0 && o != this._array[rval]; --rval) {}
        return rval;
    }
    
    public short remove(final int index) {
        if (index >= this._limit) {
            throw new IndexOutOfBoundsException();
        }
        final short rval = this._array[index];
        System.arraycopy(this._array, index + 1, this._array, index, this._limit - index);
        --this._limit;
        return rval;
    }
    
    public boolean removeValue(final short o) {
        boolean rval = false;
        for (int j = 0; !rval && j < this._limit; ++j) {
            if (o == this._array[j]) {
                System.arraycopy(this._array, j + 1, this._array, j, this._limit - j);
                --this._limit;
                rval = true;
            }
        }
        return rval;
    }
    
    public boolean removeAll(final ShortList c) {
        boolean rval = false;
        for (int j = 0; j < c._limit; ++j) {
            if (this.removeValue(c._array[j])) {
                rval = true;
            }
        }
        return rval;
    }
    
    public boolean retainAll(final ShortList c) {
        boolean rval = false;
        int j = 0;
        while (j < this._limit) {
            if (!c.contains(this._array[j])) {
                this.remove(j);
                rval = true;
            }
            else {
                ++j;
            }
        }
        return rval;
    }
    
    public short set(final int index, final short element) {
        if (index >= this._limit) {
            throw new IndexOutOfBoundsException();
        }
        final short rval = this._array[index];
        this._array[index] = element;
        return rval;
    }
    
    public int size() {
        return this._limit;
    }
    
    public short[] toArray() {
        final short[] rval = new short[this._limit];
        System.arraycopy(this._array, 0, rval, 0, this._limit);
        return rval;
    }
    
    public short[] toArray(final short[] a) {
        short[] rval;
        if (a.length == this._limit) {
            System.arraycopy(this._array, 0, a, 0, this._limit);
            rval = a;
        }
        else {
            rval = this.toArray();
        }
        return rval;
    }
    
    private void growArray(final int new_size) {
        final int size = (new_size == this._array.length) ? (new_size + 1) : new_size;
        final short[] new_array = new short[size];
        System.arraycopy(this._array, 0, new_array, 0, this._limit);
        this._array = new_array;
    }
}

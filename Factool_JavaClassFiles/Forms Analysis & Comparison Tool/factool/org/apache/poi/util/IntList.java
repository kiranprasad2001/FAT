// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.util;

public class IntList
{
    private int[] _array;
    private int _limit;
    private int fillval;
    private static final int _default_size = 128;
    
    public IntList() {
        this(128);
    }
    
    public IntList(final int initialCapacity) {
        this(initialCapacity, 0);
    }
    
    public IntList(final IntList list) {
        this(list._array.length);
        System.arraycopy(list._array, 0, this._array, 0, this._array.length);
        this._limit = list._limit;
    }
    
    public IntList(final int initialCapacity, final int fillvalue) {
        this.fillval = 0;
        this._array = new int[initialCapacity];
        if (this.fillval != 0) {
            this.fillArray(this.fillval = fillvalue, this._array, 0);
        }
        this._limit = 0;
    }
    
    private void fillArray(final int val, final int[] array, final int index) {
        for (int k = index; k < array.length; ++k) {
            array[k] = val;
        }
    }
    
    public void add(final int index, final int value) {
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
    
    public boolean add(final int value) {
        if (this._limit == this._array.length) {
            this.growArray(this._limit * 2);
        }
        this._array[this._limit++] = value;
        return true;
    }
    
    public boolean addAll(final IntList c) {
        if (c._limit != 0) {
            if (this._limit + c._limit > this._array.length) {
                this.growArray(this._limit + c._limit);
            }
            System.arraycopy(c._array, 0, this._array, this._limit, c._limit);
            this._limit += c._limit;
        }
        return true;
    }
    
    public boolean addAll(final int index, final IntList c) {
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
    
    public boolean contains(final int o) {
        boolean rval = false;
        for (int j = 0; !rval && j < this._limit; ++j) {
            if (this._array[j] == o) {
                rval = true;
            }
        }
        return rval;
    }
    
    public boolean containsAll(final IntList c) {
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
            final IntList other = (IntList)o;
            if (other._limit == this._limit) {
                rval = true;
                for (int j = 0; rval && j < this._limit; rval = (this._array[j] == other._array[j]), ++j) {}
            }
        }
        return rval;
    }
    
    public int get(final int index) {
        if (index >= this._limit) {
            throw new IndexOutOfBoundsException(index + " not accessible in a list of length " + this._limit);
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
    
    public int indexOf(final int o) {
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
    
    public int lastIndexOf(final int o) {
        int rval;
        for (rval = this._limit - 1; rval >= 0 && o != this._array[rval]; --rval) {}
        return rval;
    }
    
    public int remove(final int index) {
        if (index >= this._limit) {
            throw new IndexOutOfBoundsException();
        }
        final int rval = this._array[index];
        System.arraycopy(this._array, index + 1, this._array, index, this._limit - index);
        --this._limit;
        return rval;
    }
    
    public boolean removeValue(final int o) {
        boolean rval = false;
        for (int j = 0; !rval && j < this._limit; ++j) {
            if (o == this._array[j]) {
                if (j + 1 < this._limit) {
                    System.arraycopy(this._array, j + 1, this._array, j, this._limit - j);
                }
                --this._limit;
                rval = true;
            }
        }
        return rval;
    }
    
    public boolean removeAll(final IntList c) {
        boolean rval = false;
        for (int j = 0; j < c._limit; ++j) {
            if (this.removeValue(c._array[j])) {
                rval = true;
            }
        }
        return rval;
    }
    
    public boolean retainAll(final IntList c) {
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
    
    public int set(final int index, final int element) {
        if (index >= this._limit) {
            throw new IndexOutOfBoundsException();
        }
        final int rval = this._array[index];
        this._array[index] = element;
        return rval;
    }
    
    public int size() {
        return this._limit;
    }
    
    public int[] toArray() {
        final int[] rval = new int[this._limit];
        System.arraycopy(this._array, 0, rval, 0, this._limit);
        return rval;
    }
    
    public int[] toArray(final int[] a) {
        int[] rval;
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
        final int[] new_array = new int[size];
        if (this.fillval != 0) {
            this.fillArray(this.fillval, new_array, this._array.length);
        }
        System.arraycopy(this._array, 0, new_array, 0, this._limit);
        this._array = new_array;
    }
}

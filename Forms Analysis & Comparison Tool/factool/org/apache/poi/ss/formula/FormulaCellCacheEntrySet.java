// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula;

final class FormulaCellCacheEntrySet
{
    private static final FormulaCellCacheEntry[] EMPTY_ARRAY;
    private int _size;
    private FormulaCellCacheEntry[] _arr;
    
    public FormulaCellCacheEntrySet() {
        this._arr = FormulaCellCacheEntrySet.EMPTY_ARRAY;
    }
    
    public FormulaCellCacheEntry[] toArray() {
        final int nItems = this._size;
        if (nItems < 1) {
            return FormulaCellCacheEntrySet.EMPTY_ARRAY;
        }
        final FormulaCellCacheEntry[] result = new FormulaCellCacheEntry[nItems];
        int j = 0;
        for (int i = 0; i < this._arr.length; ++i) {
            final FormulaCellCacheEntry cce = this._arr[i];
            if (cce != null) {
                result[j++] = cce;
            }
        }
        if (j != nItems) {
            throw new IllegalStateException("size mismatch");
        }
        return result;
    }
    
    public void add(final CellCacheEntry cce) {
        if (this._size * 3 >= this._arr.length * 2) {
            final FormulaCellCacheEntry[] prevArr = this._arr;
            final FormulaCellCacheEntry[] newArr = new FormulaCellCacheEntry[4 + this._arr.length * 3 / 2];
            for (int i = 0; i < prevArr.length; ++i) {
                final FormulaCellCacheEntry prevCce = this._arr[i];
                if (prevCce != null) {
                    addInternal(newArr, prevCce);
                }
            }
            this._arr = newArr;
        }
        if (addInternal(this._arr, cce)) {
            ++this._size;
        }
    }
    
    private static boolean addInternal(final CellCacheEntry[] arr, final CellCacheEntry cce) {
        int i;
        int startIx;
        for (startIx = (i = Math.abs(cce.hashCode() % arr.length)); i < arr.length; ++i) {
            final CellCacheEntry item = arr[i];
            if (item == cce) {
                return false;
            }
            if (item == null) {
                arr[i] = cce;
                return true;
            }
        }
        for (i = 0; i < startIx; ++i) {
            final CellCacheEntry item = arr[i];
            if (item == cce) {
                return false;
            }
            if (item == null) {
                arr[i] = cce;
                return true;
            }
        }
        throw new IllegalStateException("No empty space found");
    }
    
    public boolean remove(final CellCacheEntry cce) {
        final FormulaCellCacheEntry[] arr = this._arr;
        if (this._size * 3 < this._arr.length && this._arr.length > 8) {
            boolean found = false;
            final FormulaCellCacheEntry[] prevArr = this._arr;
            final FormulaCellCacheEntry[] newArr = new FormulaCellCacheEntry[this._arr.length / 2];
            for (int i = 0; i < prevArr.length; ++i) {
                final FormulaCellCacheEntry prevCce = this._arr[i];
                if (prevCce != null) {
                    if (prevCce == cce) {
                        found = true;
                        --this._size;
                    }
                    else {
                        addInternal(newArr, prevCce);
                    }
                }
            }
            this._arr = newArr;
            return found;
        }
        int j;
        int startIx;
        for (startIx = (j = Math.abs(cce.hashCode() % arr.length)); j < arr.length; ++j) {
            final FormulaCellCacheEntry item = arr[j];
            if (item == cce) {
                arr[j] = null;
                --this._size;
                return true;
            }
        }
        for (j = 0; j < startIx; ++j) {
            final FormulaCellCacheEntry item = arr[j];
            if (item == cce) {
                arr[j] = null;
                --this._size;
                return true;
            }
        }
        return false;
    }
    
    static {
        EMPTY_ARRAY = new FormulaCellCacheEntry[0];
    }
}

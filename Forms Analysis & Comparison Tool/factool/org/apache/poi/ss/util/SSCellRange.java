// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.util;

import java.util.NoSuchElementException;
import java.util.Iterator;
import java.lang.reflect.Array;
import java.util.List;
import org.apache.poi.util.Internal;
import org.apache.poi.ss.usermodel.CellRange;
import org.apache.poi.ss.usermodel.Cell;

@Internal
public final class SSCellRange<K extends Cell> implements CellRange<K>
{
    private final int _height;
    private final int _width;
    private final K[] _flattenedArray;
    private final int _firstRow;
    private final int _firstColumn;
    
    private SSCellRange(final int firstRow, final int firstColumn, final int height, final int width, final K[] flattenedArray) {
        this._firstRow = firstRow;
        this._firstColumn = firstColumn;
        this._height = height;
        this._width = width;
        this._flattenedArray = flattenedArray;
    }
    
    public static <B extends Cell> SSCellRange<B> create(final int firstRow, final int firstColumn, final int height, final int width, final List<B> flattenedList, final Class<B> cellClass) {
        final int nItems = flattenedList.size();
        if (height * width != nItems) {
            throw new IllegalArgumentException("Array size mismatch.");
        }
        final B[] flattenedArray = (B[])Array.newInstance(cellClass, nItems);
        flattenedList.toArray(flattenedArray);
        return new SSCellRange<B>(firstRow, firstColumn, height, width, flattenedArray);
    }
    
    @Override
    public int getHeight() {
        return this._height;
    }
    
    @Override
    public int getWidth() {
        return this._width;
    }
    
    @Override
    public int size() {
        return this._height * this._width;
    }
    
    @Override
    public String getReferenceText() {
        final CellRangeAddress cra = new CellRangeAddress(this._firstRow, this._firstRow + this._height - 1, this._firstColumn, this._firstColumn + this._width - 1);
        return cra.formatAsString();
    }
    
    @Override
    public K getTopLeftCell() {
        return this._flattenedArray[0];
    }
    
    @Override
    public K getCell(final int relativeRowIndex, final int relativeColumnIndex) {
        if (relativeRowIndex < 0 || relativeRowIndex >= this._height) {
            throw new ArrayIndexOutOfBoundsException("Specified row " + relativeRowIndex + " is outside the allowable range (0.." + (this._height - 1) + ").");
        }
        if (relativeColumnIndex < 0 || relativeColumnIndex >= this._width) {
            throw new ArrayIndexOutOfBoundsException("Specified colummn " + relativeColumnIndex + " is outside the allowable range (0.." + (this._width - 1) + ").");
        }
        final int flatIndex = this._width * relativeRowIndex + relativeColumnIndex;
        return this._flattenedArray[flatIndex];
    }
    
    @Override
    public K[] getFlattenedCells() {
        return this._flattenedArray.clone();
    }
    
    @Override
    public K[][] getCells() {
        Class<?> itemCls = this._flattenedArray.getClass();
        final K[][] result = (K[][])Array.newInstance(itemCls, this._height);
        itemCls = itemCls.getComponentType();
        for (int r = this._height - 1; r >= 0; --r) {
            final K[] row = (K[])Array.newInstance(itemCls, this._width);
            final int flatIndex = this._width * r;
            System.arraycopy(this._flattenedArray, flatIndex, row, 0, this._width);
        }
        return result;
    }
    
    @Override
    public Iterator<K> iterator() {
        return new ArrayIterator<K>(this._flattenedArray);
    }
    
    private static final class ArrayIterator<D> implements Iterator<D>
    {
        private final D[] _array;
        private int _index;
        
        public ArrayIterator(final D[] array) {
            this._array = array;
            this._index = 0;
        }
        
        @Override
        public boolean hasNext() {
            return this._index < this._array.length;
        }
        
        @Override
        public D next() {
            if (this._index >= this._array.length) {
                throw new NoSuchElementException(String.valueOf(this._index));
            }
            return this._array[this._index++];
        }
        
        @Override
        public void remove() {
            throw new UnsupportedOperationException("Cannot remove cells from this CellRange.");
        }
    }
}

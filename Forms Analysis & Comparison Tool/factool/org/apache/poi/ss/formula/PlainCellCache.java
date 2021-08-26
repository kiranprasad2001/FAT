// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula;

import java.util.HashMap;
import java.util.Map;

final class PlainCellCache
{
    private Map<Loc, PlainValueCellCacheEntry> _plainValueEntriesByLoc;
    
    public PlainCellCache() {
        this._plainValueEntriesByLoc = new HashMap<Loc, PlainValueCellCacheEntry>();
    }
    
    public void put(final Loc key, final PlainValueCellCacheEntry cce) {
        this._plainValueEntriesByLoc.put(key, cce);
    }
    
    public void clear() {
        this._plainValueEntriesByLoc.clear();
    }
    
    public PlainValueCellCacheEntry get(final Loc key) {
        return this._plainValueEntriesByLoc.get(key);
    }
    
    public void remove(final Loc key) {
        this._plainValueEntriesByLoc.remove(key);
    }
    
    public static final class Loc
    {
        private final long _bookSheetColumn;
        private final int _rowIndex;
        
        public Loc(final int bookIndex, final int sheetIndex, final int rowIndex, final int columnIndex) {
            this._bookSheetColumn = toBookSheetColumn(bookIndex, sheetIndex, columnIndex);
            this._rowIndex = rowIndex;
        }
        
        public static long toBookSheetColumn(final int bookIndex, final int sheetIndex, final int columnIndex) {
            return (((long)bookIndex & 0xFFFFL) << 48) + (((long)sheetIndex & 0xFFFFL) << 32) + (((long)columnIndex & 0xFFFFL) << 0);
        }
        
        public Loc(final long bookSheetColumn, final int rowIndex) {
            this._bookSheetColumn = bookSheetColumn;
            this._rowIndex = rowIndex;
        }
        
        @Override
        public int hashCode() {
            return (int)(this._bookSheetColumn ^ this._bookSheetColumn >>> 32) + 17 * this._rowIndex;
        }
        
        @Override
        public boolean equals(final Object obj) {
            assert obj instanceof Loc : "these package-private cache key instances are only compared to themselves";
            final Loc other = (Loc)obj;
            return this._bookSheetColumn == other._bookSheetColumn && this._rowIndex == other._rowIndex;
        }
        
        public int getRowIndex() {
            return this._rowIndex;
        }
        
        public int getColumnIndex() {
            return (int)(this._bookSheetColumn & 0xFFFFL);
        }
        
        public int getSheetIndex() {
            return (int)(this._bookSheetColumn >> 32 & 0xFFFFL);
        }
        
        public int getBookIndex() {
            return (int)(this._bookSheetColumn >> 48 & 0xFFFFL);
        }
    }
}

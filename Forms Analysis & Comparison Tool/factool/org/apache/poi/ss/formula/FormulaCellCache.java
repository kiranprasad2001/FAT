// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula;

import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;

final class FormulaCellCache
{
    private final Map<Object, FormulaCellCacheEntry> _formulaEntriesByCell;
    
    public FormulaCellCache() {
        this._formulaEntriesByCell = new HashMap<Object, FormulaCellCacheEntry>();
    }
    
    public CellCacheEntry[] getCacheEntries() {
        final FormulaCellCacheEntry[] result = new FormulaCellCacheEntry[this._formulaEntriesByCell.size()];
        this._formulaEntriesByCell.values().toArray(result);
        return result;
    }
    
    public void clear() {
        this._formulaEntriesByCell.clear();
    }
    
    public FormulaCellCacheEntry get(final EvaluationCell cell) {
        return this._formulaEntriesByCell.get(cell.getIdentityKey());
    }
    
    public void put(final EvaluationCell cell, final FormulaCellCacheEntry entry) {
        this._formulaEntriesByCell.put(cell.getIdentityKey(), entry);
    }
    
    public FormulaCellCacheEntry remove(final EvaluationCell cell) {
        return this._formulaEntriesByCell.remove(cell.getIdentityKey());
    }
    
    public void applyOperation(final IEntryOperation operation) {
        final Iterator<FormulaCellCacheEntry> i = this._formulaEntriesByCell.values().iterator();
        while (i.hasNext()) {
            operation.processEntry(i.next());
        }
    }
    
    interface IEntryOperation
    {
        void processEntry(final FormulaCellCacheEntry p0);
    }
}

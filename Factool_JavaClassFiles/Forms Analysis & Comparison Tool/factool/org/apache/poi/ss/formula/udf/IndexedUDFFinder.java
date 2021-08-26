// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.udf;

import org.apache.poi.ss.formula.functions.FreeRefFunction;
import java.util.HashMap;
import org.apache.poi.util.Internal;

@Internal
public class IndexedUDFFinder extends AggregatingUDFFinder
{
    private final HashMap<Integer, String> _funcMap;
    
    public IndexedUDFFinder(final UDFFinder... usedToolPacks) {
        super(usedToolPacks);
        this._funcMap = new HashMap<Integer, String>();
    }
    
    @Override
    public FreeRefFunction findFunction(final String name) {
        final FreeRefFunction func = super.findFunction(name);
        if (func != null) {
            final int idx = this.getFunctionIndex(name);
            this._funcMap.put(idx, name);
        }
        return func;
    }
    
    public String getFunctionName(final int idx) {
        return this._funcMap.get(idx);
    }
    
    public int getFunctionIndex(final String name) {
        return name.hashCode();
    }
}

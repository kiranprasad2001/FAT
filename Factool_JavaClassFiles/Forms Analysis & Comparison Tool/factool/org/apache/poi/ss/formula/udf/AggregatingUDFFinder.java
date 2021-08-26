// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.udf;

import java.util.Iterator;
import org.apache.poi.ss.formula.functions.FreeRefFunction;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;

public class AggregatingUDFFinder implements UDFFinder
{
    private final Collection<UDFFinder> _usedToolPacks;
    
    public AggregatingUDFFinder(final UDFFinder... usedToolPacks) {
        (this._usedToolPacks = new ArrayList<UDFFinder>(usedToolPacks.length)).addAll(Arrays.asList(usedToolPacks));
    }
    
    @Override
    public FreeRefFunction findFunction(final String name) {
        for (final UDFFinder pack : this._usedToolPacks) {
            final FreeRefFunction evaluatorForFunction = pack.findFunction(name);
            if (evaluatorForFunction != null) {
                return evaluatorForFunction;
            }
        }
        return null;
    }
    
    public void add(final UDFFinder toolPack) {
        this._usedToolPacks.add(toolPack);
    }
}

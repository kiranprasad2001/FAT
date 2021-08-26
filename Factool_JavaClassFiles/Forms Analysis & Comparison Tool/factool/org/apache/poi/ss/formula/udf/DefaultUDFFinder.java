// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.udf;

import java.util.HashMap;
import org.apache.poi.ss.formula.functions.FreeRefFunction;
import java.util.Map;

public final class DefaultUDFFinder implements UDFFinder
{
    private final Map<String, FreeRefFunction> _functionsByName;
    
    public DefaultUDFFinder(final String[] functionNames, final FreeRefFunction[] functionImpls) {
        final int nFuncs = functionNames.length;
        if (functionImpls.length != nFuncs) {
            throw new IllegalArgumentException("Mismatch in number of function names and implementations");
        }
        final HashMap<String, FreeRefFunction> m = new HashMap<String, FreeRefFunction>(nFuncs * 3 / 2);
        for (int i = 0; i < functionImpls.length; ++i) {
            m.put(functionNames[i].toUpperCase(), functionImpls[i]);
        }
        this._functionsByName = m;
    }
    
    @Override
    public FreeRefFunction findFunction(final String name) {
        return this._functionsByName.get(name.toUpperCase());
    }
}

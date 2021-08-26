// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.function;

import java.util.Set;
import java.util.Map;

public final class FunctionMetadataRegistry
{
    public static final String FUNCTION_NAME_IF = "IF";
    public static final int FUNCTION_INDEX_IF = 1;
    public static final short FUNCTION_INDEX_SUM = 4;
    public static final int FUNCTION_INDEX_CHOOSE = 100;
    public static final short FUNCTION_INDEX_INDIRECT = 148;
    public static final short FUNCTION_INDEX_EXTERNAL = 255;
    private static FunctionMetadataRegistry _instance;
    private final FunctionMetadata[] _functionDataByIndex;
    private final Map<String, FunctionMetadata> _functionDataByName;
    
    private static FunctionMetadataRegistry getInstance() {
        if (FunctionMetadataRegistry._instance == null) {
            FunctionMetadataRegistry._instance = FunctionMetadataReader.createRegistry();
        }
        return FunctionMetadataRegistry._instance;
    }
    
    FunctionMetadataRegistry(final FunctionMetadata[] functionDataByIndex, final Map<String, FunctionMetadata> functionDataByName) {
        this._functionDataByIndex = functionDataByIndex;
        this._functionDataByName = functionDataByName;
    }
    
    Set<String> getAllFunctionNames() {
        return this._functionDataByName.keySet();
    }
    
    public static FunctionMetadata getFunctionByIndex(final int index) {
        return getInstance().getFunctionByIndexInternal(index);
    }
    
    private FunctionMetadata getFunctionByIndexInternal(final int index) {
        return this._functionDataByIndex[index];
    }
    
    public static short lookupIndexByName(final String name) {
        final FunctionMetadata fd = getInstance().getFunctionByNameInternal(name);
        if (fd == null) {
            return -1;
        }
        return (short)fd.getIndex();
    }
    
    private FunctionMetadata getFunctionByNameInternal(final String name) {
        return this._functionDataByName.get(name);
    }
    
    public static FunctionMetadata getFunctionByName(final String name) {
        return getInstance().getFunctionByNameInternal(name);
    }
}

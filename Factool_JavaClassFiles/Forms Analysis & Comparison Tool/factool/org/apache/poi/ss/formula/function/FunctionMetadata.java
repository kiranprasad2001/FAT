// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.function;

public final class FunctionMetadata
{
    private static final short FUNCTION_MAX_PARAMS = 30;
    private final int _index;
    private final String _name;
    private final int _minParams;
    private final int _maxParams;
    private final byte _returnClassCode;
    private final byte[] _parameterClassCodes;
    
    FunctionMetadata(final int index, final String name, final int minParams, final int maxParams, final byte returnClassCode, final byte[] parameterClassCodes) {
        this._index = index;
        this._name = name;
        this._minParams = minParams;
        this._maxParams = maxParams;
        this._returnClassCode = returnClassCode;
        this._parameterClassCodes = parameterClassCodes;
    }
    
    public int getIndex() {
        return this._index;
    }
    
    public String getName() {
        return this._name;
    }
    
    public int getMinParams() {
        return this._minParams;
    }
    
    public int getMaxParams() {
        return this._maxParams;
    }
    
    public boolean hasFixedArgsLength() {
        return this._minParams == this._maxParams;
    }
    
    public byte getReturnClassCode() {
        return this._returnClassCode;
    }
    
    public byte[] getParameterClassCodes() {
        return this._parameterClassCodes.clone();
    }
    
    public boolean hasUnlimitedVarags() {
        return 30 == this._maxParams;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer(64);
        sb.append(this.getClass().getName()).append(" [");
        sb.append(this._index).append(" ").append(this._name);
        sb.append("]");
        return sb.toString();
    }
}

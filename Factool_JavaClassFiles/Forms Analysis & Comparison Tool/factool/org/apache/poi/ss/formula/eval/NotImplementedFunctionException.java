// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.eval;

public final class NotImplementedFunctionException extends NotImplementedException
{
    private static final long serialVersionUID = 1208119411557559057L;
    private String functionName;
    
    public NotImplementedFunctionException(final String functionName) {
        super(functionName);
        this.functionName = functionName;
    }
    
    public NotImplementedFunctionException(final String functionName, final NotImplementedException cause) {
        super(functionName, cause);
        this.functionName = functionName;
    }
    
    public String getFunctionName() {
        return this.functionName;
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.eval;

public final class FunctionNameEval implements ValueEval
{
    private final String _functionName;
    
    public FunctionNameEval(final String functionName) {
        this._functionName = functionName;
    }
    
    public String getFunctionName() {
        return this._functionName;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer(64);
        sb.append(this.getClass().getName()).append(" [");
        sb.append(this._functionName);
        sb.append("]");
        return sb.toString();
    }
}

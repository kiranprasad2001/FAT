// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.eval;

import org.apache.poi.ss.formula.EvaluationName;

public final class ExternalNameEval implements ValueEval
{
    private final EvaluationName _name;
    
    public ExternalNameEval(final EvaluationName name) {
        this._name = name;
    }
    
    public EvaluationName getName() {
        return this._name;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer(64);
        sb.append(this.getClass().getName()).append(" [");
        sb.append(this._name.getNameText());
        sb.append("]");
        return sb.toString();
    }
}

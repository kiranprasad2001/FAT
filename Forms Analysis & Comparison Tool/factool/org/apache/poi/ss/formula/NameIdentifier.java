// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula;

public class NameIdentifier
{
    private final String _name;
    private final boolean _isQuoted;
    
    public NameIdentifier(final String name, final boolean isQuoted) {
        this._name = name;
        this._isQuoted = isQuoted;
    }
    
    public String getName() {
        return this._name;
    }
    
    public boolean isQuoted() {
        return this._isQuoted;
    }
    
    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer(64);
        sb.append(this.getClass().getName());
        sb.append(" [");
        if (this._isQuoted) {
            sb.append("'").append(this._name).append("'");
        }
        else {
            sb.append(this._name);
        }
        sb.append("]");
        return sb.toString();
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.eval;

public class NotImplementedException extends RuntimeException
{
    private static final long serialVersionUID = -5840703336495141301L;
    
    public NotImplementedException(final String message) {
        super(message);
    }
    
    public NotImplementedException(final String message, final NotImplementedException cause) {
        super(message, cause);
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.util;

public class RecordFormatException extends RuntimeException
{
    public RecordFormatException(final String exception) {
        super(exception);
    }
    
    public RecordFormatException(final String exception, final Throwable thr) {
        super(exception, thr);
    }
    
    public RecordFormatException(final Throwable thr) {
        super(thr);
    }
}

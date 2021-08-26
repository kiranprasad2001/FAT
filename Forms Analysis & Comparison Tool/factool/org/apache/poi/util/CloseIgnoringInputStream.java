// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.util;

import java.io.InputStream;
import java.io.FilterInputStream;

public class CloseIgnoringInputStream extends FilterInputStream
{
    public CloseIgnoringInputStream(final InputStream in) {
        super(in);
    }
    
    @Override
    public void close() {
    }
}

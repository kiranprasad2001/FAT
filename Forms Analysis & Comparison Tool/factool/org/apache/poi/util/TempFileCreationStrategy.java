// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.util;

import java.io.IOException;
import java.io.File;

public interface TempFileCreationStrategy
{
    File createTempFile(final String p0, final String p1) throws IOException;
}

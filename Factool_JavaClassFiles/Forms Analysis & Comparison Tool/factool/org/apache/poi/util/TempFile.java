// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.util;

import java.io.IOException;
import java.io.File;

public final class TempFile
{
    private static TempFileCreationStrategy strategy;
    
    public static void setTempFileCreationStrategy(final TempFileCreationStrategy strategy) {
        if (strategy == null) {
            throw new IllegalArgumentException("strategy == null");
        }
        TempFile.strategy = strategy;
    }
    
    public static File createTempFile(final String prefix, final String suffix) throws IOException {
        return TempFile.strategy.createTempFile(prefix, suffix);
    }
    
    static {
        TempFile.strategy = new DefaultTempFileCreationStrategy();
    }
    
    public static class DefaultTempFileCreationStrategy implements TempFileCreationStrategy
    {
        private File dir;
        
        public DefaultTempFileCreationStrategy() {
            this(null);
        }
        
        public DefaultTempFileCreationStrategy(final File dir) {
            this.dir = dir;
        }
        
        @Override
        public File createTempFile(final String prefix, final String suffix) throws IOException {
            if (this.dir == null) {
                (this.dir = new File(System.getProperty("java.io.tmpdir"), "poifiles")).mkdir();
                if (System.getProperty("poi.keep.tmp.files") == null) {
                    this.dir.deleteOnExit();
                }
            }
            final File newFile = File.createTempFile(prefix, suffix, this.dir);
            if (System.getProperty("poi.keep.tmp.files") == null) {
                newFile.deleteOnExit();
            }
            return newFile;
        }
    }
}

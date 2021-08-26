// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.util;

public class JvmBugs
{
    private static final POILogger LOG;
    
    public static boolean hasLineBreakMeasurerBug() {
        final String version = System.getProperty("java.version");
        final String os = System.getProperty("os.name").toLowerCase();
        final boolean ignore = Boolean.getBoolean("org.apache.poi.JvmBugs.LineBreakMeasurer.ignore");
        final boolean hasBug = !ignore && os.contains("win") && ("1.6.0_45".equals(version) || "1.7.0_21".equals(version));
        if (hasBug) {
            JvmBugs.LOG.log(5, "JVM has LineBreakMeasurer bug - see POI bug #54904 - caller code might default to Lucida Sans");
        }
        return hasBug;
    }
    
    static {
        LOG = POILogFactory.getLogger(JvmBugs.class);
    }
}

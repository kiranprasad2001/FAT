// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.util;

public class SystemOutLogger extends POILogger
{
    private String _cat;
    
    @Override
    public void initialize(final String cat) {
        this._cat = cat;
    }
    
    @Override
    public void log(final int level, final Object obj1) {
        this.log(level, obj1, null);
    }
    
    @Override
    public void log(final int level, final Object obj1, final Throwable exception) {
        if (this.check(level)) {
            System.out.println("[" + this._cat + "]" + SystemOutLogger.LEVEL_STRINGS_SHORT[Math.min(SystemOutLogger.LEVEL_STRINGS_SHORT.length - 1, level)] + " " + obj1);
            if (exception != null) {
                exception.printStackTrace(System.out);
            }
        }
    }
    
    @Override
    public boolean check(final int level) {
        int currentLevel;
        try {
            currentLevel = Integer.parseInt(System.getProperty("poi.log.level", "5"));
        }
        catch (SecurityException e) {
            currentLevel = 1;
        }
        return level >= currentLevel;
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.util;

import java.util.List;
import java.util.Collection;
import java.util.ArrayList;

public abstract class POILogger
{
    public static final int DEBUG = 1;
    public static final int INFO = 3;
    public static final int WARN = 5;
    public static final int ERROR = 7;
    public static final int FATAL = 9;
    protected static final String[] LEVEL_STRINGS_SHORT;
    protected static final String[] LEVEL_STRINGS;
    
    POILogger() {
    }
    
    public abstract void initialize(final String p0);
    
    public abstract void log(final int p0, final Object p1);
    
    public abstract void log(final int p0, final Object p1, final Throwable p2);
    
    public abstract boolean check(final int p0);
    
    public void log(final int level, final Object obj1, final Object obj2) {
        if (this.check(level)) {
            this.log(level, new StringBuffer(32).append(obj1).append(obj2));
        }
    }
    
    public void log(final int level, final Object obj1, final Object obj2, final Object obj3) {
        if (this.check(level)) {
            this.log(level, new StringBuffer(48).append(obj1).append(obj2).append(obj3));
        }
    }
    
    public void log(final int level, final Object obj1, final Object obj2, final Object obj3, final Object obj4) {
        if (this.check(level)) {
            this.log(level, new StringBuffer(64).append(obj1).append(obj2).append(obj3).append(obj4));
        }
    }
    
    public void log(final int level, final Object obj1, final Object obj2, final Object obj3, final Object obj4, final Object obj5) {
        if (this.check(level)) {
            this.log(level, new StringBuffer(80).append(obj1).append(obj2).append(obj3).append(obj4).append(obj5));
        }
    }
    
    public void log(final int level, final Object obj1, final Object obj2, final Object obj3, final Object obj4, final Object obj5, final Object obj6) {
        if (this.check(level)) {
            this.log(level, new StringBuffer(96).append(obj1).append(obj2).append(obj3).append(obj4).append(obj5).append(obj6));
        }
    }
    
    public void log(final int level, final Object obj1, final Object obj2, final Object obj3, final Object obj4, final Object obj5, final Object obj6, final Object obj7) {
        if (this.check(level)) {
            this.log(level, new StringBuffer(112).append(obj1).append(obj2).append(obj3).append(obj4).append(obj5).append(obj6).append(obj7));
        }
    }
    
    public void log(final int level, final Object obj1, final Object obj2, final Object obj3, final Object obj4, final Object obj5, final Object obj6, final Object obj7, final Object obj8) {
        if (this.check(level)) {
            this.log(level, new StringBuffer(128).append(obj1).append(obj2).append(obj3).append(obj4).append(obj5).append(obj6).append(obj7).append(obj8));
        }
    }
    
    public void log(final int level, final Throwable exception) {
        this.log(level, null, exception);
    }
    
    public void log(final int level, final Object obj1, final Object obj2, final Throwable exception) {
        if (this.check(level)) {
            this.log(level, new StringBuffer(32).append(obj1).append(obj2), exception);
        }
    }
    
    public void log(final int level, final Object obj1, final Object obj2, final Object obj3, final Throwable exception) {
        if (this.check(level)) {
            this.log(level, new StringBuffer(48).append(obj1).append(obj2).append(obj3), exception);
        }
    }
    
    public void log(final int level, final Object obj1, final Object obj2, final Object obj3, final Object obj4, final Throwable exception) {
        if (this.check(level)) {
            this.log(level, new StringBuffer(64).append(obj1).append(obj2).append(obj3).append(obj4), exception);
        }
    }
    
    public void log(final int level, final Object obj1, final Object obj2, final Object obj3, final Object obj4, final Object obj5, final Throwable exception) {
        if (this.check(level)) {
            this.log(level, new StringBuffer(80).append(obj1).append(obj2).append(obj3).append(obj4).append(obj5), exception);
        }
    }
    
    public void log(final int level, final Object obj1, final Object obj2, final Object obj3, final Object obj4, final Object obj5, final Object obj6, final Throwable exception) {
        if (this.check(level)) {
            this.log(level, new StringBuffer(96).append(obj1).append(obj2).append(obj3).append(obj4).append(obj5).append(obj6), exception);
        }
    }
    
    public void log(final int level, final Object obj1, final Object obj2, final Object obj3, final Object obj4, final Object obj5, final Object obj6, final Object obj7, final Throwable exception) {
        if (this.check(level)) {
            this.log(level, new StringBuffer(112).append(obj1).append(obj2).append(obj3).append(obj4).append(obj5).append(obj6).append(obj7), exception);
        }
    }
    
    public void log(final int level, final Object obj1, final Object obj2, final Object obj3, final Object obj4, final Object obj5, final Object obj6, final Object obj7, final Object obj8, final Throwable exception) {
        if (this.check(level)) {
            this.log(level, new StringBuffer(128).append(obj1).append(obj2).append(obj3).append(obj4).append(obj5).append(obj6).append(obj7).append(obj8), exception);
        }
    }
    
    public void logFormatted(final int level, final String message, final Object obj1) {
        this.commonLogFormatted(level, message, new Object[] { obj1 });
    }
    
    public void logFormatted(final int level, final String message, final Object obj1, final Object obj2) {
        this.commonLogFormatted(level, message, new Object[] { obj1, obj2 });
    }
    
    public void logFormatted(final int level, final String message, final Object obj1, final Object obj2, final Object obj3) {
        this.commonLogFormatted(level, message, new Object[] { obj1, obj2, obj3 });
    }
    
    public void logFormatted(final int level, final String message, final Object obj1, final Object obj2, final Object obj3, final Object obj4) {
        this.commonLogFormatted(level, message, new Object[] { obj1, obj2, obj3, obj4 });
    }
    
    private void commonLogFormatted(final int level, final String message, final Object[] unflatParams) {
        if (this.check(level)) {
            final Object[] params = this.flattenArrays(unflatParams);
            if (params[params.length - 1] instanceof Throwable) {
                this.log(level, StringUtil.format(message, params), (Throwable)params[params.length - 1]);
            }
            else {
                this.log(level, StringUtil.format(message, params));
            }
        }
    }
    
    private Object[] flattenArrays(final Object[] objects) {
        final List<Object> results = new ArrayList<Object>();
        for (int i = 0; i < objects.length; ++i) {
            results.addAll(this.objectToObjectArray(objects[i]));
        }
        return results.toArray(new Object[results.size()]);
    }
    
    private List<Object> objectToObjectArray(final Object object) {
        final List<Object> results = new ArrayList<Object>();
        if (object instanceof byte[]) {
            final byte[] array = (byte[])object;
            for (int j = 0; j < array.length; ++j) {
                results.add(array[j]);
            }
        }
        if (object instanceof char[]) {
            final char[] array2 = (char[])object;
            for (int j = 0; j < array2.length; ++j) {
                results.add(array2[j]);
            }
        }
        else if (object instanceof short[]) {
            final short[] array3 = (short[])object;
            for (int j = 0; j < array3.length; ++j) {
                results.add(array3[j]);
            }
        }
        else if (object instanceof int[]) {
            final int[] array4 = (int[])object;
            for (int j = 0; j < array4.length; ++j) {
                results.add(array4[j]);
            }
        }
        else if (object instanceof long[]) {
            final long[] array5 = (long[])object;
            for (int j = 0; j < array5.length; ++j) {
                results.add(array5[j]);
            }
        }
        else if (object instanceof float[]) {
            final float[] array6 = (float[])object;
            for (int j = 0; j < array6.length; ++j) {
                results.add(new Float(array6[j]));
            }
        }
        else if (object instanceof double[]) {
            final double[] array7 = (double[])object;
            for (int j = 0; j < array7.length; ++j) {
                results.add(new Double(array7[j]));
            }
        }
        else if (object instanceof Object[]) {
            final Object[] array8 = (Object[])object;
            for (int j = 0; j < array8.length; ++j) {
                results.add(array8[j]);
            }
        }
        else {
            results.add(object);
        }
        return results;
    }
    
    static {
        LEVEL_STRINGS_SHORT = new String[] { "?", "D", "?", "I", "?", "W", "?", "E", "?", "F", "?" };
        LEVEL_STRINGS = new String[] { "?0?", "DEBUG", "?2?", "INFO", "?4?", "WARN", "?6?", "ERROR", "?8?", "FATAL", "?10+?" };
    }
}

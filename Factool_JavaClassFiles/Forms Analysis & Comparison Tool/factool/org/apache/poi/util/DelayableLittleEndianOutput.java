// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.util;

public interface DelayableLittleEndianOutput extends LittleEndianOutput
{
    LittleEndianOutput createDelayedOutput(final int p0);
}

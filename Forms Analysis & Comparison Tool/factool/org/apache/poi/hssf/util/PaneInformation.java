// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.hssf.util;

public class PaneInformation
{
    public static final byte PANE_LOWER_RIGHT = 0;
    public static final byte PANE_UPPER_RIGHT = 1;
    public static final byte PANE_LOWER_LEFT = 2;
    public static final byte PANE_UPPER_LEFT = 3;
    private short x;
    private short y;
    private short topRow;
    private short leftColumn;
    private byte activePane;
    private boolean frozen;
    
    public PaneInformation(final short x, final short y, final short top, final short left, final byte active, final boolean frozen) {
        this.frozen = false;
        this.x = x;
        this.y = y;
        this.topRow = top;
        this.leftColumn = left;
        this.activePane = active;
        this.frozen = frozen;
    }
    
    public short getVerticalSplitPosition() {
        return this.x;
    }
    
    public short getHorizontalSplitPosition() {
        return this.y;
    }
    
    public short getHorizontalSplitTopRow() {
        return this.topRow;
    }
    
    public short getVerticalSplitLeftColumn() {
        return this.leftColumn;
    }
    
    public byte getActivePane() {
        return this.activePane;
    }
    
    public boolean isFreezePane() {
        return this.frozen;
    }
}

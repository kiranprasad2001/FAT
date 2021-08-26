// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.hssf.usermodel;

import org.apache.poi.ddf.EscherRecord;
import org.apache.poi.ddf.EscherClientAnchorRecord;
import org.apache.poi.ddf.EscherChildAnchorRecord;
import org.apache.poi.ddf.EscherContainerRecord;

public abstract class HSSFAnchor
{
    protected boolean _isHorizontallyFlipped;
    protected boolean _isVerticallyFlipped;
    
    public HSSFAnchor() {
        this._isHorizontallyFlipped = false;
        this._isVerticallyFlipped = false;
        this.createEscherAnchor();
    }
    
    public HSSFAnchor(final int dx1, final int dy1, final int dx2, final int dy2) {
        this._isHorizontallyFlipped = false;
        this._isVerticallyFlipped = false;
        this.createEscherAnchor();
        this.setDx1(dx1);
        this.setDy1(dy1);
        this.setDx2(dx2);
        this.setDy2(dy2);
    }
    
    public static HSSFAnchor createAnchorFromEscher(final EscherContainerRecord container) {
        if (null != container.getChildById((short)(-4081))) {
            return new HSSFChildAnchor(container.getChildById((short)(-4081)));
        }
        if (null != container.getChildById((short)(-4080))) {
            return new HSSFClientAnchor(container.getChildById((short)(-4080)));
        }
        return null;
    }
    
    public abstract int getDx1();
    
    public abstract void setDx1(final int p0);
    
    public abstract int getDy1();
    
    public abstract void setDy1(final int p0);
    
    public abstract int getDy2();
    
    public abstract void setDy2(final int p0);
    
    public abstract int getDx2();
    
    public abstract void setDx2(final int p0);
    
    public abstract boolean isHorizontallyFlipped();
    
    public abstract boolean isVerticallyFlipped();
    
    protected abstract EscherRecord getEscherAnchor();
    
    protected abstract void createEscherAnchor();
}

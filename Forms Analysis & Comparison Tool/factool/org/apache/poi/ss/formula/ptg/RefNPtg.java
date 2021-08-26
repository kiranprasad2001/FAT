// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.ptg;

import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.LittleEndianInput;

public final class RefNPtg extends Ref2DPtgBase
{
    public static final byte sid = 44;
    
    public RefNPtg(final LittleEndianInput in) {
        super(in);
    }
    
    @Override
    protected byte getSid() {
        return 44;
    }
}

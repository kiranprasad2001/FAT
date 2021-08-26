// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.ptg;

import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.ss.formula.function.FunctionMetadataRegistry;
import org.apache.poi.ss.formula.function.FunctionMetadata;
import org.apache.poi.util.LittleEndianInput;

public final class FuncPtg extends AbstractFunctionPtg
{
    public static final byte sid = 33;
    public static final int SIZE = 3;
    
    public static FuncPtg create(final LittleEndianInput in) {
        return create(in.readUShort());
    }
    
    private FuncPtg(final int funcIndex, final FunctionMetadata fm) {
        super(funcIndex, fm.getReturnClassCode(), fm.getParameterClassCodes(), fm.getMinParams());
    }
    
    public static FuncPtg create(final int functionIndex) {
        final FunctionMetadata fm = FunctionMetadataRegistry.getFunctionByIndex(functionIndex);
        if (fm == null) {
            throw new RuntimeException("Invalid built-in function index (" + functionIndex + ")");
        }
        return new FuncPtg(functionIndex, fm);
    }
    
    @Override
    public void write(final LittleEndianOutput out) {
        out.writeByte(33 + this.getPtgClass());
        out.writeShort(this.getFunctionIndex());
    }
    
    @Override
    public int getSize() {
        return 3;
    }
}

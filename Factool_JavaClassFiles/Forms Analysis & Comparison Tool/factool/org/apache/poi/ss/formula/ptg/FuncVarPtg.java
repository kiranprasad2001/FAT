// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.ptg;

import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.ss.formula.function.FunctionMetadata;
import org.apache.poi.ss.formula.function.FunctionMetadataRegistry;
import org.apache.poi.util.LittleEndianInput;

public final class FuncVarPtg extends AbstractFunctionPtg
{
    public static final byte sid = 34;
    private static final int SIZE = 4;
    public static final OperationPtg SUM;
    
    private FuncVarPtg(final int functionIndex, final int returnClass, final byte[] paramClasses, final int numArgs) {
        super(functionIndex, returnClass, paramClasses, numArgs);
    }
    
    public static FuncVarPtg create(final LittleEndianInput in) {
        return create(in.readByte(), in.readShort());
    }
    
    public static FuncVarPtg create(final String pName, final int numArgs) {
        return create(numArgs, AbstractFunctionPtg.lookupIndex(pName));
    }
    
    private static FuncVarPtg create(final int numArgs, final int functionIndex) {
        final FunctionMetadata fm = FunctionMetadataRegistry.getFunctionByIndex(functionIndex);
        if (fm == null) {
            return new FuncVarPtg(functionIndex, 32, new byte[] { 32 }, numArgs);
        }
        return new FuncVarPtg(functionIndex, fm.getReturnClassCode(), fm.getParameterClassCodes(), numArgs);
    }
    
    @Override
    public void write(final LittleEndianOutput out) {
        out.writeByte(34 + this.getPtgClass());
        out.writeByte(this.getNumberOfOperands());
        out.writeShort(this.getFunctionIndex());
    }
    
    @Override
    public int getSize() {
        return 4;
    }
    
    static {
        SUM = create("SUM", 1);
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.eval;

public final class BlankEval implements ValueEval
{
    public static final BlankEval instance;
    @Deprecated
    public static final BlankEval INSTANCE;
    
    private BlankEval() {
    }
    
    static {
        instance = new BlankEval();
        INSTANCE = BlankEval.instance;
    }
}

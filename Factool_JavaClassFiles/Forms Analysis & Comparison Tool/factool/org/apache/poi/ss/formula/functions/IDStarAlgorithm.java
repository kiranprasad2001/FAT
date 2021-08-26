// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.functions;

import org.apache.poi.ss.formula.eval.ValueEval;

public interface IDStarAlgorithm
{
    void reset();
    
    boolean processMatch(final ValueEval p0);
    
    ValueEval getResult();
}

// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula;

import org.apache.poi.ss.formula.eval.ValueEval;

interface IEvaluationListener
{
    void onCacheHit(final int p0, final int p1, final int p2, final ValueEval p3);
    
    void onReadPlainValue(final int p0, final int p1, final int p2, final ICacheEntry p3);
    
    void onStartEvaluate(final EvaluationCell p0, final ICacheEntry p1);
    
    void onEndEvaluate(final ICacheEntry p0, final ValueEval p1);
    
    void onClearWholeCache();
    
    void onClearCachedValue(final ICacheEntry p0);
    
    void sortDependentCachedValues(final ICacheEntry[] p0);
    
    void onClearDependentCachedValue(final ICacheEntry p0, final int p1);
    
    void onChangeFromBlankValue(final int p0, final int p1, final int p2, final EvaluationCell p3, final ICacheEntry p4);
    
    public interface ICacheEntry
    {
        ValueEval getValue();
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula.udf;

import org.apache.poi.ss.formula.atp.AnalysisToolPak;
import org.apache.poi.ss.formula.functions.FreeRefFunction;

public interface UDFFinder
{
    public static final UDFFinder DEFAULT = new AggregatingUDFFinder(new UDFFinder[] { AnalysisToolPak.instance });
    
    FreeRefFunction findFunction(final String p0);
}

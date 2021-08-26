// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.formula;

public interface IStabilityClassifier
{
    public static final IStabilityClassifier TOTALLY_IMMUTABLE = new IStabilityClassifier() {
        @Override
        public boolean isCellFinal(final int sheetIndex, final int rowIndex, final int columnIndex) {
            return true;
        }
    };
    
    boolean isCellFinal(final int p0, final int p1, final int p2);
}

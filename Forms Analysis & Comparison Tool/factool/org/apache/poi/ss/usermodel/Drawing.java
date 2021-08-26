// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.ss.usermodel;

public interface Drawing
{
    Picture createPicture(final ClientAnchor p0, final int p1);
    
    Comment createCellComment(final ClientAnchor p0);
    
    Chart createChart(final ClientAnchor p0);
    
    ClientAnchor createAnchor(final int p0, final int p1, final int p2, final int p3, final int p4, final int p5, final int p6, final int p7);
}

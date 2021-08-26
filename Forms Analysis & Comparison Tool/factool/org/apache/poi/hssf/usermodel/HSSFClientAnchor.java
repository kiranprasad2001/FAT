// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.hssf.usermodel;

import org.apache.poi.ddf.EscherRecord;
import org.apache.poi.ddf.EscherClientAnchorRecord;
import org.apache.poi.ss.usermodel.ClientAnchor;

public final class HSSFClientAnchor extends HSSFAnchor implements ClientAnchor
{
    private EscherClientAnchorRecord _escherClientAnchor;
    
    public HSSFClientAnchor(final EscherClientAnchorRecord escherClientAnchorRecord) {
        this._escherClientAnchor = escherClientAnchorRecord;
    }
    
    public HSSFClientAnchor() {
    }
    
    public HSSFClientAnchor(final int dx1, final int dy1, final int dx2, final int dy2, final short col1, final int row1, final short col2, final int row2) {
        super(dx1, dy1, dx2, dy2);
        this.checkRange(dx1, 0, 1023, "dx1");
        this.checkRange(dx2, 0, 1023, "dx2");
        this.checkRange(dy1, 0, 255, "dy1");
        this.checkRange(dy2, 0, 255, "dy2");
        this.checkRange(col1, 0, 255, "col1");
        this.checkRange(col2, 0, 255, "col2");
        this.checkRange(row1, 0, 65280, "row1");
        this.checkRange(row2, 0, 65280, "row2");
        this.setCol1((short)Math.min(col1, col2));
        this.setCol2((short)Math.max(col1, col2));
        this.setRow1((short)Math.min(row1, row2));
        this.setRow2((short)Math.max(row1, row2));
        if (col1 > col2) {
            this._isHorizontallyFlipped = true;
        }
        if (row1 > row2) {
            this._isVerticallyFlipped = true;
        }
    }
    
    public float getAnchorHeightInPoints(final HSSFSheet sheet) {
        final int y1 = this.getDy1();
        final int y2 = this.getDy2();
        final int row1 = Math.min(this.getRow1(), this.getRow2());
        final int row2 = Math.max(this.getRow1(), this.getRow2());
        float points = 0.0f;
        if (row1 == row2) {
            points = (y2 - y1) / 256.0f * this.getRowHeightInPoints(sheet, row2);
        }
        else {
            points += (256.0f - y1) / 256.0f * this.getRowHeightInPoints(sheet, row1);
            for (int i = row1 + 1; i < row2; ++i) {
                points += this.getRowHeightInPoints(sheet, i);
            }
            points += y2 / 256.0f * this.getRowHeightInPoints(sheet, row2);
        }
        return points;
    }
    
    private float getRowHeightInPoints(final HSSFSheet sheet, final int rowNum) {
        final HSSFRow row = sheet.getRow(rowNum);
        if (row == null) {
            return sheet.getDefaultRowHeightInPoints();
        }
        return row.getHeightInPoints();
    }
    
    @Override
    public short getCol1() {
        return this._escherClientAnchor.getCol1();
    }
    
    public void setCol1(final short col1) {
        this.checkRange(col1, 0, 255, "col1");
        this._escherClientAnchor.setCol1(col1);
    }
    
    @Override
    public void setCol1(final int col1) {
        this.setCol1((short)col1);
    }
    
    @Override
    public short getCol2() {
        return this._escherClientAnchor.getCol2();
    }
    
    public void setCol2(final short col2) {
        this.checkRange(col2, 0, 255, "col2");
        this._escherClientAnchor.setCol2(col2);
    }
    
    @Override
    public void setCol2(final int col2) {
        this.setCol2((short)col2);
    }
    
    @Override
    public int getRow1() {
        return this._escherClientAnchor.getRow1();
    }
    
    @Override
    public void setRow1(final int row1) {
        this.checkRange(row1, 0, 65536, "row1");
        this._escherClientAnchor.setRow1(row1.shortValue());
    }
    
    @Override
    public int getRow2() {
        return this._escherClientAnchor.getRow2();
    }
    
    @Override
    public void setRow2(final int row2) {
        this.checkRange(row2, 0, 65536, "row2");
        this._escherClientAnchor.setRow2(row2.shortValue());
    }
    
    public void setAnchor(final short col1, final int row1, final int x1, final int y1, final short col2, final int row2, final int x2, final int y2) {
        this.checkRange(this.getDx1(), 0, 1023, "dx1");
        this.checkRange(this.getDx2(), 0, 1023, "dx2");
        this.checkRange(this.getDy1(), 0, 255, "dy1");
        this.checkRange(this.getDy2(), 0, 255, "dy2");
        this.checkRange(this.getCol1(), 0, 255, "col1");
        this.checkRange(this.getCol2(), 0, 255, "col2");
        this.checkRange(this.getRow1(), 0, 65280, "row1");
        this.checkRange(this.getRow2(), 0, 65280, "row2");
        this.setCol1(col1);
        this.setRow1(row1);
        this.setDx1(x1);
        this.setDy1(y1);
        this.setCol2(col2);
        this.setRow2(row2);
        this.setDx2(x2);
        this.setDy2(y2);
    }
    
    @Override
    public boolean isHorizontallyFlipped() {
        return this._isHorizontallyFlipped;
    }
    
    @Override
    public boolean isVerticallyFlipped() {
        return this._isVerticallyFlipped;
    }
    
    @Override
    protected EscherRecord getEscherAnchor() {
        return this._escherClientAnchor;
    }
    
    @Override
    protected void createEscherAnchor() {
        this._escherClientAnchor = new EscherClientAnchorRecord();
    }
    
    @Override
    public int getAnchorType() {
        return this._escherClientAnchor.getFlag();
    }
    
    @Override
    public void setAnchorType(final int anchorType) {
        this._escherClientAnchor.setFlag(anchorType.shortValue());
    }
    
    private void checkRange(final int value, final int minRange, final int maxRange, final String varName) {
        if (value < minRange || value > maxRange) {
            throw new IllegalArgumentException(varName + " must be between " + minRange + " and " + maxRange + ", but was: " + value);
        }
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != this.getClass()) {
            return false;
        }
        final HSSFClientAnchor anchor = (HSSFClientAnchor)obj;
        return anchor.getCol1() == this.getCol1() && anchor.getCol2() == this.getCol2() && anchor.getDx1() == this.getDx1() && anchor.getDx2() == this.getDx2() && anchor.getDy1() == this.getDy1() && anchor.getDy2() == this.getDy2() && anchor.getRow1() == this.getRow1() && anchor.getRow2() == this.getRow2() && anchor.getAnchorType() == this.getAnchorType();
    }
    
    @Override
    public int hashCode() {
        assert false : "hashCode not designed";
        return 42;
    }
    
    @Override
    public int getDx1() {
        return this._escherClientAnchor.getDx1();
    }
    
    @Override
    public void setDx1(final int dx1) {
        this._escherClientAnchor.setDx1(dx1.shortValue());
    }
    
    @Override
    public int getDy1() {
        return this._escherClientAnchor.getDy1();
    }
    
    @Override
    public void setDy1(final int dy1) {
        this._escherClientAnchor.setDy1(dy1.shortValue());
    }
    
    @Override
    public int getDy2() {
        return this._escherClientAnchor.getDy2();
    }
    
    @Override
    public void setDy2(final int dy2) {
        this._escherClientAnchor.setDy2(dy2.shortValue());
    }
    
    @Override
    public int getDx2() {
        return this._escherClientAnchor.getDx2();
    }
    
    @Override
    public void setDx2(final int dx2) {
        this._escherClientAnchor.setDx2(dx2.shortValue());
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.hssf.usermodel;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.poi.util.LittleEndian;
import java.io.ByteArrayOutputStream;
import org.apache.poi.ddf.EscherBoolProperty;
import org.apache.poi.ddf.EscherSimpleProperty;
import org.apache.poi.ddf.EscherProperty;
import org.apache.poi.ddf.EscherRGBProperty;
import org.apache.poi.ddf.EscherChildAnchorRecord;
import org.apache.poi.ddf.EscherRecord;
import org.apache.poi.ddf.EscherClientAnchorRecord;
import org.apache.poi.hssf.record.CommonObjectDataSubRecord;
import org.apache.poi.ddf.EscherSpRecord;
import org.apache.poi.ddf.EscherOptRecord;
import org.apache.poi.hssf.record.ObjRecord;
import org.apache.poi.ddf.EscherContainerRecord;

public abstract class HSSFShape
{
    public static final int LINEWIDTH_ONE_PT = 12700;
    public static final int LINEWIDTH_DEFAULT = 9525;
    public static final int LINESTYLE__COLOR_DEFAULT = 134217792;
    public static final int FILL__FILLCOLOR_DEFAULT = 134217737;
    public static final boolean NO_FILL_DEFAULT = true;
    public static final int LINESTYLE_SOLID = 0;
    public static final int LINESTYLE_DASHSYS = 1;
    public static final int LINESTYLE_DOTSYS = 2;
    public static final int LINESTYLE_DASHDOTSYS = 3;
    public static final int LINESTYLE_DASHDOTDOTSYS = 4;
    public static final int LINESTYLE_DOTGEL = 5;
    public static final int LINESTYLE_DASHGEL = 6;
    public static final int LINESTYLE_LONGDASHGEL = 7;
    public static final int LINESTYLE_DASHDOTGEL = 8;
    public static final int LINESTYLE_LONGDASHDOTGEL = 9;
    public static final int LINESTYLE_LONGDASHDOTDOTGEL = 10;
    public static final int LINESTYLE_NONE = -1;
    public static final int LINESTYLE_DEFAULT = -1;
    private HSSFShape parent;
    HSSFAnchor anchor;
    private HSSFPatriarch _patriarch;
    private final EscherContainerRecord _escherContainer;
    private final ObjRecord _objRecord;
    private final EscherOptRecord _optRecord;
    public static final int NO_FILLHITTEST_TRUE = 1114112;
    public static final int NO_FILLHITTEST_FALSE = 65536;
    
    public HSSFShape(final EscherContainerRecord spContainer, final ObjRecord objRecord) {
        this._escherContainer = spContainer;
        this._objRecord = objRecord;
        this._optRecord = spContainer.getChildById((short)(-4085));
        this.anchor = HSSFAnchor.createAnchorFromEscher(spContainer);
    }
    
    public HSSFShape(final HSSFShape parent, final HSSFAnchor anchor) {
        this.parent = parent;
        this.anchor = anchor;
        this._escherContainer = this.createSpContainer();
        this._optRecord = this._escherContainer.getChildById((short)(-4085));
        this._objRecord = this.createObjRecord();
    }
    
    protected abstract EscherContainerRecord createSpContainer();
    
    protected abstract ObjRecord createObjRecord();
    
    protected abstract void afterRemove(final HSSFPatriarch p0);
    
    void setShapeId(final int shapeId) {
        final EscherSpRecord spRecord = this._escherContainer.getChildById((short)(-4086));
        spRecord.setShapeId(shapeId);
        final CommonObjectDataSubRecord cod = this._objRecord.getSubRecords().get(0);
        cod.setObjectId((short)(shapeId % 1024));
    }
    
    int getShapeId() {
        return this._escherContainer.getChildById((short)(-4086)).getShapeId();
    }
    
    abstract void afterInsert(final HSSFPatriarch p0);
    
    protected EscherContainerRecord getEscherContainer() {
        return this._escherContainer;
    }
    
    protected ObjRecord getObjRecord() {
        return this._objRecord;
    }
    
    protected EscherOptRecord getOptRecord() {
        return this._optRecord;
    }
    
    public HSSFShape getParent() {
        return this.parent;
    }
    
    public HSSFAnchor getAnchor() {
        return this.anchor;
    }
    
    public void setAnchor(final HSSFAnchor anchor) {
        int i = 0;
        int recordId = -1;
        if (this.parent == null) {
            if (anchor instanceof HSSFChildAnchor) {
                throw new IllegalArgumentException("Must use client anchors for shapes directly attached to sheet.");
            }
            final EscherClientAnchorRecord anch = this._escherContainer.getChildById((short)(-4080));
            if (null != anch) {
                for (i = 0; i < this._escherContainer.getChildRecords().size(); ++i) {
                    if (this._escherContainer.getChild(i).getRecordId() == -4080 && i != this._escherContainer.getChildRecords().size() - 1) {
                        recordId = this._escherContainer.getChild(i + 1).getRecordId();
                    }
                }
                this._escherContainer.removeChildRecord(anch);
            }
        }
        else {
            if (anchor instanceof HSSFClientAnchor) {
                throw new IllegalArgumentException("Must use child anchors for shapes attached to groups.");
            }
            final EscherChildAnchorRecord anch2 = this._escherContainer.getChildById((short)(-4081));
            if (null != anch2) {
                for (i = 0; i < this._escherContainer.getChildRecords().size(); ++i) {
                    if (this._escherContainer.getChild(i).getRecordId() == -4081 && i != this._escherContainer.getChildRecords().size() - 1) {
                        recordId = this._escherContainer.getChild(i + 1).getRecordId();
                    }
                }
                this._escherContainer.removeChildRecord(anch2);
            }
        }
        if (-1 == recordId) {
            this._escherContainer.addChildRecord(anchor.getEscherAnchor());
        }
        else {
            this._escherContainer.addChildBefore(anchor.getEscherAnchor(), recordId);
        }
        this.anchor = anchor;
    }
    
    public int getLineStyleColor() {
        final EscherRGBProperty rgbProperty = this._optRecord.lookup(448);
        return (rgbProperty == null) ? 134217792 : rgbProperty.getRgbColor();
    }
    
    public void setLineStyleColor(final int lineStyleColor) {
        this.setPropertyValue(new EscherRGBProperty((short)448, lineStyleColor));
    }
    
    public void setLineStyleColor(final int red, final int green, final int blue) {
        final int lineStyleColor = blue << 16 | green << 8 | red;
        this.setPropertyValue(new EscherRGBProperty((short)448, lineStyleColor));
    }
    
    public int getFillColor() {
        final EscherRGBProperty rgbProperty = this._optRecord.lookup(385);
        return (rgbProperty == null) ? 134217737 : rgbProperty.getRgbColor();
    }
    
    public void setFillColor(final int fillColor) {
        this.setPropertyValue(new EscherRGBProperty((short)385, fillColor));
    }
    
    public void setFillColor(final int red, final int green, final int blue) {
        final int fillColor = blue << 16 | green << 8 | red;
        this.setPropertyValue(new EscherRGBProperty((short)385, fillColor));
    }
    
    public int getLineWidth() {
        final EscherSimpleProperty property = this._optRecord.lookup(459);
        return (property == null) ? 9525 : property.getPropertyValue();
    }
    
    public void setLineWidth(final int lineWidth) {
        this.setPropertyValue(new EscherSimpleProperty((short)459, lineWidth));
    }
    
    public int getLineStyle() {
        final EscherSimpleProperty property = this._optRecord.lookup(462);
        if (null == property) {
            return -1;
        }
        return property.getPropertyValue();
    }
    
    public void setLineStyle(final int lineStyle) {
        this.setPropertyValue(new EscherSimpleProperty((short)462, lineStyle));
        if (this.getLineStyle() != 0) {
            this.setPropertyValue(new EscherSimpleProperty((short)471, 0));
            if (this.getLineStyle() == -1) {
                this.setPropertyValue(new EscherBoolProperty((short)511, 524288));
            }
            else {
                this.setPropertyValue(new EscherBoolProperty((short)511, 524296));
            }
        }
    }
    
    public boolean isNoFill() {
        final EscherBoolProperty property = this._optRecord.lookup(447);
        return property == null || property.getPropertyValue() == 1114112;
    }
    
    public void setNoFill(final boolean noFill) {
        this.setPropertyValue(new EscherBoolProperty((short)447, noFill ? 1114112 : 65536));
    }
    
    protected void setPropertyValue(final EscherProperty property) {
        this._optRecord.setEscherProperty(property);
    }
    
    public void setFlipVertical(final boolean value) {
        final EscherSpRecord sp = this.getEscherContainer().getChildById((short)(-4086));
        if (value) {
            sp.setFlags(sp.getFlags() | 0x80);
        }
        else {
            sp.setFlags(sp.getFlags() & 0x7FFFFF7F);
        }
    }
    
    public void setFlipHorizontal(final boolean value) {
        final EscherSpRecord sp = this.getEscherContainer().getChildById((short)(-4086));
        if (value) {
            sp.setFlags(sp.getFlags() | 0x40);
        }
        else {
            sp.setFlags(sp.getFlags() & 0x7FFFFFBF);
        }
    }
    
    public boolean isFlipVertical() {
        final EscherSpRecord sp = this.getEscherContainer().getChildById((short)(-4086));
        return (sp.getFlags() & 0x80) != 0x0;
    }
    
    public boolean isFlipHorizontal() {
        final EscherSpRecord sp = this.getEscherContainer().getChildById((short)(-4086));
        return (sp.getFlags() & 0x40) != 0x0;
    }
    
    public int getRotationDegree() {
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        final EscherSimpleProperty property = this.getOptRecord().lookup(4);
        if (null == property) {
            return 0;
        }
        try {
            LittleEndian.putInt(property.getPropertyValue(), bos);
            return LittleEndian.getShort(bos.toByteArray(), 2);
        }
        catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }
    
    public void setRotationDegree(final short value) {
        this.setPropertyValue(new EscherSimpleProperty((short)4, value << 16));
    }
    
    public int countOfAllChildren() {
        return 1;
    }
    
    protected abstract HSSFShape cloneShape();
    
    protected void setPatriarch(final HSSFPatriarch _patriarch) {
        this._patriarch = _patriarch;
    }
    
    public HSSFPatriarch getPatriarch() {
        return this._patriarch;
    }
    
    protected void setParent(final HSSFShape parent) {
        this.parent = parent;
    }
}

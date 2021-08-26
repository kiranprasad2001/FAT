// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.hssf.usermodel;

import org.apache.poi.ddf.EscherRecordFactory;
import org.apache.poi.ddf.DefaultEscherRecordFactory;
import org.apache.poi.hssf.record.EscherAggregate;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.hssf.record.SubRecord;
import org.apache.poi.hssf.record.EndSubRecord;
import org.apache.poi.hssf.record.CommonObjectDataSubRecord;
import org.apache.poi.ddf.EscherRecord;
import org.apache.poi.ddf.EscherTextboxRecord;
import org.apache.poi.ddf.EscherShapePathProperty;
import org.apache.poi.ddf.EscherRGBProperty;
import org.apache.poi.ddf.EscherBoolProperty;
import org.apache.poi.ddf.EscherProperty;
import org.apache.poi.ddf.EscherSimpleProperty;
import org.apache.poi.ddf.EscherOptRecord;
import org.apache.poi.ddf.EscherClientDataRecord;
import org.apache.poi.ddf.EscherSpRecord;
import org.apache.poi.hssf.record.ObjRecord;
import org.apache.poi.ddf.EscherContainerRecord;
import org.apache.poi.hssf.record.TextObjectRecord;

public class HSSFSimpleShape extends HSSFShape
{
    public static final short OBJECT_TYPE_LINE = 20;
    public static final short OBJECT_TYPE_RECTANGLE = 1;
    public static final short OBJECT_TYPE_OVAL = 3;
    public static final short OBJECT_TYPE_ARC = 19;
    public static final short OBJECT_TYPE_PICTURE = 75;
    public static final short OBJECT_TYPE_COMBO_BOX = 201;
    public static final short OBJECT_TYPE_COMMENT = 202;
    public static final short OBJECT_TYPE_MICROSOFT_OFFICE_DRAWING = 30;
    public static final int WRAP_SQUARE = 0;
    public static final int WRAP_BY_POINTS = 1;
    public static final int WRAP_NONE = 2;
    private TextObjectRecord _textObjectRecord;
    
    public HSSFSimpleShape(final EscherContainerRecord spContainer, final ObjRecord objRecord, final TextObjectRecord textObjectRecord) {
        super(spContainer, objRecord);
        this._textObjectRecord = textObjectRecord;
    }
    
    public HSSFSimpleShape(final EscherContainerRecord spContainer, final ObjRecord objRecord) {
        super(spContainer, objRecord);
    }
    
    public HSSFSimpleShape(final HSSFShape parent, final HSSFAnchor anchor) {
        super(parent, anchor);
        this._textObjectRecord = this.createTextObjRecord();
    }
    
    protected TextObjectRecord getTextObjectRecord() {
        return this._textObjectRecord;
    }
    
    protected TextObjectRecord createTextObjRecord() {
        final TextObjectRecord obj = new TextObjectRecord();
        obj.setHorizontalTextAlignment(2);
        obj.setVerticalTextAlignment(2);
        obj.setTextLocked(true);
        obj.setTextOrientation(0);
        obj.setStr(new HSSFRichTextString(""));
        return obj;
    }
    
    @Override
    protected EscherContainerRecord createSpContainer() {
        final EscherContainerRecord spContainer = new EscherContainerRecord();
        spContainer.setRecordId((short)(-4092));
        spContainer.setOptions((short)15);
        final EscherSpRecord sp = new EscherSpRecord();
        sp.setRecordId((short)(-4086));
        sp.setFlags(2560);
        sp.setVersion((short)2);
        final EscherClientDataRecord clientData = new EscherClientDataRecord();
        clientData.setRecordId((short)(-4079));
        clientData.setOptions((short)0);
        final EscherOptRecord optRecord = new EscherOptRecord();
        optRecord.setEscherProperty(new EscherSimpleProperty((short)462, 0));
        optRecord.setEscherProperty(new EscherBoolProperty((short)511, 524296));
        optRecord.setEscherProperty(new EscherRGBProperty((short)385, 134217737));
        optRecord.setEscherProperty(new EscherRGBProperty((short)448, 134217792));
        optRecord.setEscherProperty(new EscherBoolProperty((short)447, 65536));
        optRecord.setEscherProperty(new EscherBoolProperty((short)511, 524296));
        optRecord.setEscherProperty(new EscherShapePathProperty((short)324, 4));
        optRecord.setEscherProperty(new EscherBoolProperty((short)959, 524288));
        optRecord.setRecordId((short)(-4085));
        final EscherTextboxRecord escherTextbox = new EscherTextboxRecord();
        escherTextbox.setRecordId((short)(-4083));
        escherTextbox.setOptions((short)0);
        spContainer.addChildRecord(sp);
        spContainer.addChildRecord(optRecord);
        spContainer.addChildRecord(this.getAnchor().getEscherAnchor());
        spContainer.addChildRecord(clientData);
        spContainer.addChildRecord(escherTextbox);
        return spContainer;
    }
    
    @Override
    protected ObjRecord createObjRecord() {
        final ObjRecord obj = new ObjRecord();
        final CommonObjectDataSubRecord c = new CommonObjectDataSubRecord();
        c.setLocked(true);
        c.setPrintable(true);
        c.setAutofill(true);
        c.setAutoline(true);
        final EndSubRecord e = new EndSubRecord();
        obj.addSubRecord(c);
        obj.addSubRecord(e);
        return obj;
    }
    
    @Override
    protected void afterRemove(final HSSFPatriarch patriarch) {
        patriarch._getBoundAggregate().removeShapeToObjRecord(this.getEscherContainer().getChildById((short)(-4079)));
        if (null != this.getEscherContainer().getChildById((short)(-4083))) {
            patriarch._getBoundAggregate().removeShapeToObjRecord(this.getEscherContainer().getChildById((short)(-4083)));
        }
    }
    
    public HSSFRichTextString getString() {
        return this._textObjectRecord.getStr();
    }
    
    public void setString(final RichTextString string) {
        if (this.getShapeType() == 0 || this.getShapeType() == 20) {
            throw new IllegalStateException("Cannot set text for shape type: " + this.getShapeType());
        }
        final HSSFRichTextString rtr = (HSSFRichTextString)string;
        if (rtr.numFormattingRuns() == 0) {
            rtr.applyFont((short)0);
        }
        final TextObjectRecord txo = this.getOrCreateTextObjRecord();
        txo.setStr(rtr);
        if (string.getString() != null) {
            this.setPropertyValue(new EscherSimpleProperty((short)128, string.getString().hashCode()));
        }
    }
    
    @Override
    void afterInsert(final HSSFPatriarch patriarch) {
        final EscherAggregate agg = patriarch._getBoundAggregate();
        agg.associateShapeToObjRecord(this.getEscherContainer().getChildById((short)(-4079)), this.getObjRecord());
        if (null != this.getTextObjectRecord()) {
            agg.associateShapeToObjRecord(this.getEscherContainer().getChildById((short)(-4083)), this.getTextObjectRecord());
        }
    }
    
    @Override
    protected HSSFShape cloneShape() {
        TextObjectRecord txo = null;
        final EscherContainerRecord spContainer = new EscherContainerRecord();
        final byte[] inSp = this.getEscherContainer().serialize();
        spContainer.fillFields(inSp, 0, new DefaultEscherRecordFactory());
        final ObjRecord obj = (ObjRecord)this.getObjRecord().cloneViaReserialise();
        if (this.getTextObjectRecord() != null && this.getString() != null && null != this.getString().getString()) {
            txo = (TextObjectRecord)this.getTextObjectRecord().cloneViaReserialise();
        }
        return new HSSFSimpleShape(spContainer, obj, txo);
    }
    
    public int getShapeType() {
        final EscherSpRecord spRecord = this.getEscherContainer().getChildById((short)(-4086));
        return spRecord.getShapeType();
    }
    
    public int getWrapText() {
        final EscherSimpleProperty property = this.getOptRecord().lookup(133);
        return (null == property) ? 0 : property.getPropertyValue();
    }
    
    public void setWrapText(final int value) {
        this.setPropertyValue(new EscherSimpleProperty((short)133, false, false, value));
    }
    
    public void setShapeType(final int value) {
        final CommonObjectDataSubRecord cod = this.getObjRecord().getSubRecords().get(0);
        cod.setObjectType((short)30);
        final EscherSpRecord spRecord = this.getEscherContainer().getChildById((short)(-4086));
        spRecord.setShapeType((short)value);
    }
    
    private TextObjectRecord getOrCreateTextObjRecord() {
        if (this.getTextObjectRecord() == null) {
            this._textObjectRecord = this.createTextObjRecord();
        }
        EscherTextboxRecord escherTextbox = this.getEscherContainer().getChildById((short)(-4083));
        if (null == escherTextbox) {
            escherTextbox = new EscherTextboxRecord();
            escherTextbox.setRecordId((short)(-4083));
            escherTextbox.setOptions((short)0);
            this.getEscherContainer().addChildRecord(escherTextbox);
            this.getPatriarch()._getBoundAggregate().associateShapeToObjRecord(escherTextbox, this._textObjectRecord);
        }
        return this._textObjectRecord;
    }
}

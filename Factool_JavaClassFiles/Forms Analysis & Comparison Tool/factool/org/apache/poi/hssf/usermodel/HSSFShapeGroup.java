// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.hssf.usermodel;

import org.apache.poi.ddf.EscherRecordFactory;
import org.apache.poi.ddf.DefaultEscherRecordFactory;
import org.apache.poi.hssf.record.EscherAggregate;
import org.apache.poi.hssf.record.Record;
import java.util.Collection;
import java.util.Collections;
import org.apache.poi.hssf.record.SubRecord;
import org.apache.poi.hssf.record.EndSubRecord;
import org.apache.poi.hssf.record.GroupMarkerSubRecord;
import org.apache.poi.hssf.record.CommonObjectDataSubRecord;
import org.apache.poi.ddf.EscherProperty;
import org.apache.poi.ddf.EscherBoolProperty;
import org.apache.poi.ddf.EscherClientDataRecord;
import org.apache.poi.ddf.EscherOptRecord;
import org.apache.poi.ddf.EscherSpRecord;
import java.util.Iterator;
import org.apache.poi.ddf.EscherChildAnchorRecord;
import org.apache.poi.ddf.EscherClientAnchorRecord;
import org.apache.poi.ddf.EscherRecord;
import java.util.ArrayList;
import org.apache.poi.hssf.record.ObjRecord;
import org.apache.poi.ddf.EscherContainerRecord;
import org.apache.poi.ddf.EscherSpgrRecord;
import java.util.List;

public class HSSFShapeGroup extends HSSFShape implements HSSFShapeContainer
{
    private final List<HSSFShape> shapes;
    private EscherSpgrRecord _spgrRecord;
    
    public HSSFShapeGroup(final EscherContainerRecord spgrContainer, final ObjRecord objRecord) {
        super(spgrContainer, objRecord);
        this.shapes = new ArrayList<HSSFShape>();
        final EscherContainerRecord spContainer = spgrContainer.getChildContainers().get(0);
        this._spgrRecord = (EscherSpgrRecord)spContainer.getChild(0);
        for (final EscherRecord ch : spContainer.getChildRecords()) {
            switch (ch.getRecordId()) {
                case -4087: {
                    continue;
                }
                case -4080: {
                    this.anchor = new HSSFClientAnchor((EscherClientAnchorRecord)ch);
                    continue;
                }
                case -4081: {
                    this.anchor = new HSSFChildAnchor((EscherChildAnchorRecord)ch);
                    continue;
                }
            }
        }
    }
    
    public HSSFShapeGroup(final HSSFShape parent, final HSSFAnchor anchor) {
        super(parent, anchor);
        this.shapes = new ArrayList<HSSFShape>();
        this._spgrRecord = ((EscherContainerRecord)this.getEscherContainer().getChild(0)).getChildById((short)(-4087));
    }
    
    @Override
    protected EscherContainerRecord createSpContainer() {
        final EscherContainerRecord spgrContainer = new EscherContainerRecord();
        final EscherContainerRecord spContainer = new EscherContainerRecord();
        final EscherSpgrRecord spgr = new EscherSpgrRecord();
        final EscherSpRecord sp = new EscherSpRecord();
        final EscherOptRecord opt = new EscherOptRecord();
        final EscherClientDataRecord clientData = new EscherClientDataRecord();
        spgrContainer.setRecordId((short)(-4093));
        spgrContainer.setOptions((short)15);
        spContainer.setRecordId((short)(-4092));
        spContainer.setOptions((short)15);
        spgr.setRecordId((short)(-4087));
        spgr.setOptions((short)1);
        spgr.setRectX1(0);
        spgr.setRectY1(0);
        spgr.setRectX2(1023);
        spgr.setRectY2(255);
        sp.setRecordId((short)(-4086));
        sp.setOptions((short)2);
        if (this.getAnchor() instanceof HSSFClientAnchor) {
            sp.setFlags(513);
        }
        else {
            sp.setFlags(515);
        }
        opt.setRecordId((short)(-4085));
        opt.setOptions((short)35);
        opt.addEscherProperty(new EscherBoolProperty((short)127, 262148));
        opt.addEscherProperty(new EscherBoolProperty((short)959, 524288));
        final EscherRecord anchor = this.getAnchor().getEscherAnchor();
        clientData.setRecordId((short)(-4079));
        clientData.setOptions((short)0);
        spgrContainer.addChildRecord(spContainer);
        spContainer.addChildRecord(spgr);
        spContainer.addChildRecord(sp);
        spContainer.addChildRecord(opt);
        spContainer.addChildRecord(anchor);
        spContainer.addChildRecord(clientData);
        return spgrContainer;
    }
    
    @Override
    protected ObjRecord createObjRecord() {
        final ObjRecord obj = new ObjRecord();
        final CommonObjectDataSubRecord cmo = new CommonObjectDataSubRecord();
        cmo.setObjectType((short)0);
        cmo.setLocked(true);
        cmo.setPrintable(true);
        cmo.setAutofill(true);
        cmo.setAutoline(true);
        final GroupMarkerSubRecord gmo = new GroupMarkerSubRecord();
        final EndSubRecord end = new EndSubRecord();
        obj.addSubRecord(cmo);
        obj.addSubRecord(gmo);
        obj.addSubRecord(end);
        return obj;
    }
    
    @Override
    protected void afterRemove(final HSSFPatriarch patriarch) {
        patriarch._getBoundAggregate().removeShapeToObjRecord(this.getEscherContainer().getChildContainers().get(0).getChildById((short)(-4079)));
        for (int i = 0; i < this.shapes.size(); ++i) {
            final HSSFShape shape = this.shapes.get(i);
            this.removeShape(shape);
            shape.afterRemove(this.getPatriarch());
        }
        this.shapes.clear();
    }
    
    private void onCreate(final HSSFShape shape) {
        if (this.getPatriarch() != null) {
            final EscherContainerRecord spContainer = shape.getEscherContainer();
            final int shapeId = this.getPatriarch().newShapeId();
            shape.setShapeId(shapeId);
            this.getEscherContainer().addChildRecord(spContainer);
            shape.afterInsert(this.getPatriarch());
            EscherSpRecord sp;
            if (shape instanceof HSSFShapeGroup) {
                sp = shape.getEscherContainer().getChildContainers().get(0).getChildById((short)(-4086));
            }
            else {
                sp = shape.getEscherContainer().getChildById((short)(-4086));
            }
            sp.setFlags(sp.getFlags() | 0x2);
        }
    }
    
    public HSSFShapeGroup createGroup(final HSSFChildAnchor anchor) {
        final HSSFShapeGroup group = new HSSFShapeGroup(this, anchor);
        group.setParent(this);
        group.setAnchor(anchor);
        this.shapes.add(group);
        this.onCreate(group);
        return group;
    }
    
    @Override
    public void addShape(final HSSFShape shape) {
        shape.setPatriarch(this.getPatriarch());
        shape.setParent(this);
        this.shapes.add(shape);
    }
    
    public HSSFSimpleShape createShape(final HSSFChildAnchor anchor) {
        final HSSFSimpleShape shape = new HSSFSimpleShape(this, anchor);
        shape.setParent(this);
        shape.setAnchor(anchor);
        this.shapes.add(shape);
        this.onCreate(shape);
        final EscherSpRecord sp = shape.getEscherContainer().getChildById((short)(-4086));
        if (shape.getAnchor().isHorizontallyFlipped()) {
            sp.setFlags(sp.getFlags() | 0x40);
        }
        if (shape.getAnchor().isVerticallyFlipped()) {
            sp.setFlags(sp.getFlags() | 0x80);
        }
        return shape;
    }
    
    public HSSFTextbox createTextbox(final HSSFChildAnchor anchor) {
        final HSSFTextbox shape = new HSSFTextbox(this, anchor);
        shape.setParent(this);
        shape.setAnchor(anchor);
        this.shapes.add(shape);
        this.onCreate(shape);
        return shape;
    }
    
    public HSSFPolygon createPolygon(final HSSFChildAnchor anchor) {
        final HSSFPolygon shape = new HSSFPolygon(this, anchor);
        shape.setParent(this);
        shape.setAnchor(anchor);
        this.shapes.add(shape);
        this.onCreate(shape);
        return shape;
    }
    
    public HSSFPicture createPicture(final HSSFChildAnchor anchor, final int pictureIndex) {
        final HSSFPicture shape = new HSSFPicture(this, anchor);
        shape.setParent(this);
        shape.setAnchor(anchor);
        shape.setPictureIndex(pictureIndex);
        this.shapes.add(shape);
        this.onCreate(shape);
        final EscherSpRecord sp = shape.getEscherContainer().getChildById((short)(-4086));
        if (shape.getAnchor().isHorizontallyFlipped()) {
            sp.setFlags(sp.getFlags() | 0x40);
        }
        if (shape.getAnchor().isVerticallyFlipped()) {
            sp.setFlags(sp.getFlags() | 0x80);
        }
        return shape;
    }
    
    @Override
    public List<HSSFShape> getChildren() {
        return Collections.unmodifiableList((List<? extends HSSFShape>)this.shapes);
    }
    
    @Override
    public void setCoordinates(final int x1, final int y1, final int x2, final int y2) {
        this._spgrRecord.setRectX1(x1);
        this._spgrRecord.setRectX2(x2);
        this._spgrRecord.setRectY1(y1);
        this._spgrRecord.setRectY2(y2);
    }
    
    @Override
    public void clear() {
        final ArrayList<HSSFShape> copy = new ArrayList<HSSFShape>(this.shapes);
        for (final HSSFShape shape : copy) {
            this.removeShape(shape);
        }
    }
    
    @Override
    public int getX1() {
        return this._spgrRecord.getRectX1();
    }
    
    @Override
    public int getY1() {
        return this._spgrRecord.getRectY1();
    }
    
    @Override
    public int getX2() {
        return this._spgrRecord.getRectX2();
    }
    
    @Override
    public int getY2() {
        return this._spgrRecord.getRectY2();
    }
    
    @Override
    public int countOfAllChildren() {
        int count = this.shapes.size();
        for (final HSSFShape shape : this.shapes) {
            count += shape.countOfAllChildren();
        }
        return count;
    }
    
    @Override
    void afterInsert(final HSSFPatriarch patriarch) {
        final EscherAggregate agg = patriarch._getBoundAggregate();
        final EscherContainerRecord containerRecord = this.getEscherContainer().getChildById((short)(-4092));
        agg.associateShapeToObjRecord(containerRecord.getChildById((short)(-4079)), this.getObjRecord());
    }
    
    @Override
    void setShapeId(final int shapeId) {
        final EscherContainerRecord containerRecord = this.getEscherContainer().getChildById((short)(-4092));
        final EscherSpRecord spRecord = containerRecord.getChildById((short)(-4086));
        spRecord.setShapeId(shapeId);
        final CommonObjectDataSubRecord cod = this.getObjRecord().getSubRecords().get(0);
        cod.setObjectId((short)(shapeId % 1024));
    }
    
    @Override
    int getShapeId() {
        final EscherContainerRecord containerRecord = this.getEscherContainer().getChildById((short)(-4092));
        return containerRecord.getChildById((short)(-4086)).getShapeId();
    }
    
    @Override
    protected HSSFShape cloneShape() {
        throw new IllegalStateException("Use method cloneShape(HSSFPatriarch patriarch)");
    }
    
    protected HSSFShape cloneShape(final HSSFPatriarch patriarch) {
        final EscherContainerRecord spgrContainer = new EscherContainerRecord();
        spgrContainer.setRecordId((short)(-4093));
        spgrContainer.setOptions((short)15);
        final EscherContainerRecord spContainer = new EscherContainerRecord();
        final EscherContainerRecord cont = this.getEscherContainer().getChildById((short)(-4092));
        final byte[] inSp = cont.serialize();
        spContainer.fillFields(inSp, 0, new DefaultEscherRecordFactory());
        spgrContainer.addChildRecord(spContainer);
        ObjRecord obj = null;
        if (null != this.getObjRecord()) {
            obj = (ObjRecord)this.getObjRecord().cloneViaReserialise();
        }
        final HSSFShapeGroup group = new HSSFShapeGroup(spgrContainer, obj);
        group.setPatriarch(patriarch);
        for (final HSSFShape shape : this.getChildren()) {
            HSSFShape newShape;
            if (shape instanceof HSSFShapeGroup) {
                newShape = ((HSSFShapeGroup)shape).cloneShape(patriarch);
            }
            else {
                newShape = shape.cloneShape();
            }
            group.addShape(newShape);
            group.onCreate(newShape);
        }
        return group;
    }
    
    @Override
    public boolean removeShape(final HSSFShape shape) {
        final boolean isRemoved = this.getEscherContainer().removeChildRecord(shape.getEscherContainer());
        if (isRemoved) {
            shape.afterRemove(this.getPatriarch());
            this.shapes.remove(shape);
        }
        return isRemoved;
    }
    
    @Override
    public Iterator<HSSFShape> iterator() {
        return this.shapes.iterator();
    }
}

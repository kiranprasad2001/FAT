// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.hssf.usermodel;

import org.apache.poi.ss.usermodel.Picture;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.Chart;
import org.apache.poi.util.StringUtil;
import org.apache.poi.ddf.EscherComplexProperty;
import org.apache.poi.ddf.EscherProperty;
import org.apache.poi.ddf.EscherOptRecord;
import org.apache.poi.ddf.EscherDgRecord;
import java.util.Collection;
import org.apache.poi.util.Internal;
import java.util.Collections;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.ddf.EscherSpRecord;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import java.io.FileNotFoundException;
import org.apache.poi.util.HexDump;
import org.apache.poi.hssf.record.EndSubRecord;
import org.apache.poi.hssf.record.EmbeddedObjectRefSubRecord;
import org.apache.poi.hssf.record.FtPioGrbitSubRecord;
import org.apache.poi.hssf.record.FtCfSubRecord;
import org.apache.poi.hssf.record.SubRecord;
import org.apache.poi.hssf.record.CommonObjectDataSubRecord;
import org.apache.poi.hssf.record.ObjRecord;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.hssf.model.DrawingManager2;
import org.apache.poi.ddf.EscherRecord;
import java.util.Set;
import java.util.Map;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.hssf.record.NoteRecord;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ArrayList;
import org.apache.poi.hssf.record.EscherAggregate;
import org.apache.poi.ddf.EscherContainerRecord;
import org.apache.poi.ddf.EscherSpgrRecord;
import java.util.List;
import org.apache.poi.ss.usermodel.Drawing;

public final class HSSFPatriarch implements HSSFShapeContainer, Drawing
{
    private final List<HSSFShape> _shapes;
    private final EscherSpgrRecord _spgrRecord;
    private final EscherContainerRecord _mainSpgrContainer;
    private EscherAggregate _boundAggregate;
    private final HSSFSheet _sheet;
    
    HSSFPatriarch(final HSSFSheet sheet, final EscherAggregate boundAggregate) {
        this._shapes = new ArrayList<HSSFShape>();
        this._sheet = sheet;
        this._boundAggregate = boundAggregate;
        this._mainSpgrContainer = this._boundAggregate.getEscherContainer().getChildContainers().get(0);
        final EscherContainerRecord spContainer = (EscherContainerRecord)this._boundAggregate.getEscherContainer().getChildContainers().get(0).getChild(0);
        this._spgrRecord = spContainer.getChildById((short)(-4087));
        this.buildShapeTree();
    }
    
    static HSSFPatriarch createPatriarch(final HSSFPatriarch patriarch, final HSSFSheet sheet) {
        final HSSFPatriarch newPatriarch = new HSSFPatriarch(sheet, new EscherAggregate(true));
        newPatriarch.afterCreate();
        for (final HSSFShape shape : patriarch.getChildren()) {
            HSSFShape newShape;
            if (shape instanceof HSSFShapeGroup) {
                newShape = ((HSSFShapeGroup)shape).cloneShape(newPatriarch);
            }
            else {
                newShape = shape.cloneShape();
            }
            newPatriarch.onCreate(newShape);
            newPatriarch.addShape(newShape);
        }
        return newPatriarch;
    }
    
    protected void preSerialize() {
        final Map<Integer, NoteRecord> tailRecords = this._boundAggregate.getTailRecords();
        final Set<String> coordinates = new HashSet<String>(tailRecords.size());
        for (final NoteRecord rec : tailRecords.values()) {
            final String noteRef = new CellReference(rec.getRow(), rec.getColumn()).formatAsString();
            if (coordinates.contains(noteRef)) {
                throw new IllegalStateException("found multiple cell comments for cell " + noteRef);
            }
            coordinates.add(noteRef);
        }
    }
    
    @Override
    public boolean removeShape(final HSSFShape shape) {
        final boolean isRemoved = this._mainSpgrContainer.removeChildRecord(shape.getEscherContainer());
        if (isRemoved) {
            shape.afterRemove(this);
            this._shapes.remove(shape);
        }
        return isRemoved;
    }
    
    void afterCreate() {
        final DrawingManager2 drawingManager = this._sheet.getWorkbook().getWorkbook().getDrawingManager();
        final short dgId = drawingManager.findNewDrawingGroupId();
        this._boundAggregate.setDgId(dgId);
        this._boundAggregate.setMainSpRecordId(this.newShapeId());
        drawingManager.incrementDrawingsSaved();
    }
    
    public HSSFShapeGroup createGroup(final HSSFClientAnchor anchor) {
        final HSSFShapeGroup group = new HSSFShapeGroup(null, anchor);
        this.addShape(group);
        this.onCreate(group);
        return group;
    }
    
    public HSSFSimpleShape createSimpleShape(final HSSFClientAnchor anchor) {
        final HSSFSimpleShape shape = new HSSFSimpleShape(null, anchor);
        this.addShape(shape);
        this.onCreate(shape);
        return shape;
    }
    
    public HSSFPicture createPicture(final HSSFClientAnchor anchor, final int pictureIndex) {
        final HSSFPicture shape = new HSSFPicture(null, anchor);
        shape.setPictureIndex(pictureIndex);
        this.addShape(shape);
        this.onCreate(shape);
        return shape;
    }
    
    @Override
    public HSSFPicture createPicture(final ClientAnchor anchor, final int pictureIndex) {
        return this.createPicture((HSSFClientAnchor)anchor, pictureIndex);
    }
    
    public HSSFObjectData createObjectData(final HSSFClientAnchor anchor, final int storageId, final int pictureIndex) {
        final ObjRecord obj = new ObjRecord();
        final CommonObjectDataSubRecord ftCmo = new CommonObjectDataSubRecord();
        ftCmo.setObjectType((short)8);
        ftCmo.setLocked(true);
        ftCmo.setPrintable(true);
        ftCmo.setAutofill(true);
        ftCmo.setAutoline(true);
        ftCmo.setReserved1(0);
        ftCmo.setReserved2(0);
        ftCmo.setReserved3(0);
        obj.addSubRecord(ftCmo);
        final FtCfSubRecord ftCf = new FtCfSubRecord();
        final HSSFPictureData pictData = this.getSheet().getWorkbook().getAllPictures().get(pictureIndex - 1);
        switch (pictData.getFormat()) {
            case 2:
            case 3: {
                ftCf.setFlags((short)2);
                break;
            }
            case 4:
            case 5:
            case 6:
            case 7: {
                ftCf.setFlags((short)9);
                break;
            }
        }
        obj.addSubRecord(ftCf);
        final FtPioGrbitSubRecord ftPioGrbit = new FtPioGrbitSubRecord();
        ftPioGrbit.setFlagByBit(1, true);
        obj.addSubRecord(ftPioGrbit);
        final EmbeddedObjectRefSubRecord ftPictFmla = new EmbeddedObjectRefSubRecord();
        ftPictFmla.setUnknownFormulaData(new byte[] { 2, 0, 0, 0, 0 });
        ftPictFmla.setOleClassname("Paket");
        ftPictFmla.setStorageId(storageId);
        obj.addSubRecord(ftPictFmla);
        obj.addSubRecord(new EndSubRecord());
        final String entryName = "MBD" + HexDump.toHex(storageId);
        DirectoryEntry oleRoot;
        try {
            final DirectoryNode dn = this._sheet.getWorkbook().getRootDirectory();
            if (dn == null) {
                throw new FileNotFoundException();
            }
            oleRoot = (DirectoryEntry)dn.getEntry(entryName);
        }
        catch (FileNotFoundException e) {
            throw new IllegalStateException("trying to add ole shape without actually adding data first - use HSSFWorkbook.addOlePackage first", e);
        }
        final HSSFPicture shape = new HSSFPicture(null, anchor);
        shape.setPictureIndex(pictureIndex);
        final EscherContainerRecord spContainer = shape.getEscherContainer();
        final EscherSpRecord spRecord = spContainer.getChildById((short)(-4086));
        spRecord.setFlags(spRecord.getFlags() | 0x10);
        final HSSFObjectData oleShape = new HSSFObjectData(spContainer, obj, oleRoot);
        this.addShape(oleShape);
        this.onCreate(oleShape);
        return oleShape;
    }
    
    public HSSFPolygon createPolygon(final HSSFClientAnchor anchor) {
        final HSSFPolygon shape = new HSSFPolygon(null, anchor);
        this.addShape(shape);
        this.onCreate(shape);
        return shape;
    }
    
    public HSSFTextbox createTextbox(final HSSFClientAnchor anchor) {
        final HSSFTextbox shape = new HSSFTextbox(null, anchor);
        this.addShape(shape);
        this.onCreate(shape);
        return shape;
    }
    
    public HSSFComment createComment(final HSSFAnchor anchor) {
        final HSSFComment shape = new HSSFComment(null, anchor);
        this.addShape(shape);
        this.onCreate(shape);
        return shape;
    }
    
    HSSFSimpleShape createComboBox(final HSSFAnchor anchor) {
        final HSSFCombobox shape = new HSSFCombobox(null, anchor);
        this.addShape(shape);
        this.onCreate(shape);
        return shape;
    }
    
    @Override
    public HSSFComment createCellComment(final ClientAnchor anchor) {
        return this.createComment((HSSFAnchor)anchor);
    }
    
    @Override
    public List<HSSFShape> getChildren() {
        return Collections.unmodifiableList((List<? extends HSSFShape>)this._shapes);
    }
    
    @Internal
    @Override
    public void addShape(final HSSFShape shape) {
        shape.setPatriarch(this);
        this._shapes.add(shape);
    }
    
    private void onCreate(final HSSFShape shape) {
        final EscherContainerRecord spgrContainer = this._boundAggregate.getEscherContainer().getChildContainers().get(0);
        final EscherContainerRecord spContainer = shape.getEscherContainer();
        final int shapeId = this.newShapeId();
        shape.setShapeId(shapeId);
        spgrContainer.addChildRecord(spContainer);
        shape.afterInsert(this);
        this.setFlipFlags(shape);
    }
    
    public int countOfAllChildren() {
        int count = this._shapes.size();
        for (final HSSFShape shape : this._shapes) {
            count += shape.countOfAllChildren();
        }
        return count;
    }
    
    @Override
    public void setCoordinates(final int x1, final int y1, final int x2, final int y2) {
        this._spgrRecord.setRectY1(y1);
        this._spgrRecord.setRectY2(y2);
        this._spgrRecord.setRectX1(x1);
        this._spgrRecord.setRectX2(x2);
    }
    
    @Override
    public void clear() {
        final ArrayList<HSSFShape> copy = new ArrayList<HSSFShape>(this._shapes);
        for (final HSSFShape shape : copy) {
            this.removeShape(shape);
        }
    }
    
    int newShapeId() {
        final DrawingManager2 dm = this._sheet.getWorkbook().getWorkbook().getDrawingManager();
        final EscherDgRecord dg = this._boundAggregate.getEscherContainer().getChildById((short)(-4088));
        final short drawingGroupId = dg.getDrawingGroupId();
        return dm.allocateShapeId(drawingGroupId, dg);
    }
    
    public boolean containsChart() {
        final EscherOptRecord optRecord = (EscherOptRecord)this._boundAggregate.findFirstWithId((short)(-4085));
        if (optRecord == null) {
            return false;
        }
        for (final EscherProperty prop : optRecord.getEscherProperties()) {
            if (prop.getPropertyNumber() == 896 && prop.isComplex()) {
                final EscherComplexProperty cp = (EscherComplexProperty)prop;
                final String str = StringUtil.getFromUnicodeLE(cp.getComplexData());
                if (str.equals("Chart 1\u0000")) {
                    return true;
                }
                continue;
            }
        }
        return false;
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
    
    protected EscherAggregate _getBoundAggregate() {
        return this._boundAggregate;
    }
    
    @Override
    public HSSFClientAnchor createAnchor(final int dx1, final int dy1, final int dx2, final int dy2, final int col1, final int row1, final int col2, final int row2) {
        return new HSSFClientAnchor(dx1, dy1, dx2, dy2, (short)col1, row1, (short)col2, row2);
    }
    
    @Override
    public Chart createChart(final ClientAnchor anchor) {
        throw new RuntimeException("NotImplemented");
    }
    
    void buildShapeTree() {
        final EscherContainerRecord dgContainer = this._boundAggregate.getEscherContainer();
        if (dgContainer == null) {
            return;
        }
        final EscherContainerRecord spgrConrainer = dgContainer.getChildContainers().get(0);
        final List<EscherContainerRecord> spgrChildren = spgrConrainer.getChildContainers();
        for (int i = 0; i < spgrChildren.size(); ++i) {
            final EscherContainerRecord spContainer = spgrChildren.get(i);
            if (i != 0) {
                HSSFShapeFactory.createShapeTree(spContainer, this._boundAggregate, this, this._sheet.getWorkbook().getRootDirectory());
            }
        }
    }
    
    private void setFlipFlags(final HSSFShape shape) {
        final EscherSpRecord sp = shape.getEscherContainer().getChildById((short)(-4086));
        if (shape.getAnchor().isHorizontallyFlipped()) {
            sp.setFlags(sp.getFlags() | 0x40);
        }
        if (shape.getAnchor().isVerticallyFlipped()) {
            sp.setFlags(sp.getFlags() | 0x80);
        }
    }
    
    @Override
    public Iterator<HSSFShape> iterator() {
        return this._shapes.iterator();
    }
    
    protected HSSFSheet getSheet() {
        return this._sheet;
    }
}

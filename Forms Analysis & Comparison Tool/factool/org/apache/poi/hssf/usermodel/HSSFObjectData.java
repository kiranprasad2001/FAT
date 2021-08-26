// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.hssf.usermodel;

import org.apache.poi.ddf.EscherRecordFactory;
import org.apache.poi.ddf.DefaultEscherRecordFactory;
import org.apache.poi.ddf.EscherBSERecord;
import org.apache.poi.hssf.record.EscherAggregate;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.SubRecord;
import java.util.Iterator;
import org.apache.poi.poifs.filesystem.Entry;
import org.apache.poi.hssf.record.EmbeddedObjectRefSubRecord;
import java.io.IOException;
import org.apache.poi.util.HexDump;
import org.apache.poi.hssf.record.ObjRecord;
import org.apache.poi.ddf.EscherContainerRecord;
import org.apache.poi.poifs.filesystem.DirectoryEntry;

public final class HSSFObjectData extends HSSFPicture
{
    private final DirectoryEntry _root;
    
    public HSSFObjectData(final EscherContainerRecord spContainer, final ObjRecord objRecord, final DirectoryEntry _root) {
        super(spContainer, objRecord);
        this._root = _root;
    }
    
    public String getOLE2ClassName() {
        return this.findObjectRecord().getOLEClassName();
    }
    
    public DirectoryEntry getDirectory() throws IOException {
        final EmbeddedObjectRefSubRecord subRecord = this.findObjectRecord();
        final int streamId = subRecord.getStreamId();
        final String streamName = "MBD" + HexDump.toHex(streamId);
        final Entry entry = this._root.getEntry(streamName);
        if (entry instanceof DirectoryEntry) {
            return (DirectoryEntry)entry;
        }
        throw new IOException("Stream " + streamName + " was not an OLE2 directory");
    }
    
    public byte[] getObjectData() {
        return this.findObjectRecord().getObjectData();
    }
    
    public boolean hasDirectoryEntry() {
        final EmbeddedObjectRefSubRecord subRecord = this.findObjectRecord();
        final Integer streamId = subRecord.getStreamId();
        return streamId != null && streamId != 0;
    }
    
    protected EmbeddedObjectRefSubRecord findObjectRecord() {
        for (final Object subRecord : this.getObjRecord().getSubRecords()) {
            if (subRecord instanceof EmbeddedObjectRefSubRecord) {
                return (EmbeddedObjectRefSubRecord)subRecord;
            }
        }
        throw new IllegalStateException("Object data does not contain a reference to an embedded object OLE2 directory");
    }
    
    @Override
    protected EscherContainerRecord createSpContainer() {
        throw new IllegalStateException("HSSFObjectData cannot be created from scratch");
    }
    
    @Override
    protected ObjRecord createObjRecord() {
        throw new IllegalStateException("HSSFObjectData cannot be created from scratch");
    }
    
    @Override
    protected void afterRemove(final HSSFPatriarch patriarch) {
        throw new IllegalStateException("HSSFObjectData cannot be created from scratch");
    }
    
    @Override
    void afterInsert(final HSSFPatriarch patriarch) {
        final EscherAggregate agg = patriarch._getBoundAggregate();
        agg.associateShapeToObjRecord(this.getEscherContainer().getChildById((short)(-4079)), this.getObjRecord());
        final EscherBSERecord bse = patriarch.getSheet().getWorkbook().getWorkbook().getBSERecord(this.getPictureIndex());
        bse.setRef(bse.getRef() + 1);
    }
    
    @Override
    protected HSSFShape cloneShape() {
        final EscherContainerRecord spContainer = new EscherContainerRecord();
        final byte[] inSp = this.getEscherContainer().serialize();
        spContainer.fillFields(inSp, 0, new DefaultEscherRecordFactory());
        final ObjRecord obj = (ObjRecord)this.getObjRecord().cloneViaReserialise();
        return new HSSFObjectData(spContainer, obj, this._root);
    }
}

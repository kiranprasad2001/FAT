// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.hssf.usermodel;

import org.apache.poi.hssf.record.EmbeddedObjectRefSubRecord;
import org.apache.poi.hssf.record.SubRecord;
import org.apache.poi.ddf.EscherProperty;
import java.util.Iterator;
import org.apache.poi.hssf.record.Record;
import java.util.Map;
import java.util.List;
import org.apache.poi.ddf.EscherOptRecord;
import org.apache.poi.hssf.record.CommonObjectDataSubRecord;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.hssf.record.TextObjectRecord;
import org.apache.poi.ddf.EscherRecord;
import org.apache.poi.hssf.record.ObjRecord;
import org.apache.poi.ddf.EscherClientDataRecord;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.hssf.record.EscherAggregate;
import org.apache.poi.ddf.EscherContainerRecord;

public class HSSFShapeFactory
{
    private static final short OBJECT_TYPE_LINE = 1;
    private static final short OBJECT_TYPE_RECTANGLE = 2;
    private static final short OBJECT_TYPE_OVAL = 3;
    private static final short OBJECT_TYPE_ARC = 4;
    private static final short OBJECT_TYPE_PICTURE = 8;
    
    public static void createShapeTree(final EscherContainerRecord container, final EscherAggregate agg, final HSSFShapeContainer out, final DirectoryNode root) {
        if (container.getRecordId() == -4093) {
            ObjRecord obj = null;
            final EscherClientDataRecord clientData = ((EscherContainerRecord)container.getChild(0)).getChildById((short)(-4079));
            if (null != clientData) {
                obj = agg.getShapeToObjMapping().get(clientData);
            }
            final HSSFShapeGroup group = new HSSFShapeGroup(container, obj);
            final List<EscherContainerRecord> children = container.getChildContainers();
            for (int i = 0; i < children.size(); ++i) {
                final EscherContainerRecord spContainer = children.get(i);
                if (i != 0) {
                    createShapeTree(spContainer, agg, group, root);
                }
            }
            out.addShape(group);
        }
        else if (container.getRecordId() == -4092) {
            final Map<EscherRecord, Record> shapeToObj = agg.getShapeToObjMapping();
            ObjRecord objRecord = null;
            TextObjectRecord txtRecord = null;
            for (final EscherRecord record : container.getChildRecords()) {
                switch (record.getRecordId()) {
                    case -4079: {
                        objRecord = shapeToObj.get(record);
                        continue;
                    }
                    case -4083: {
                        txtRecord = shapeToObj.get(record);
                        continue;
                    }
                }
            }
            if (isEmbeddedObject(objRecord)) {
                final HSSFObjectData objectData = new HSSFObjectData(container, objRecord, root);
                out.addShape(objectData);
                return;
            }
            final CommonObjectDataSubRecord cmo = objRecord.getSubRecords().get(0);
            HSSFShape shape = null;
            switch (cmo.getObjectType()) {
                case 8: {
                    shape = new HSSFPicture(container, objRecord);
                    break;
                }
                case 2: {
                    shape = new HSSFSimpleShape(container, objRecord, txtRecord);
                    break;
                }
                case 1: {
                    shape = new HSSFSimpleShape(container, objRecord);
                    break;
                }
                case 20: {
                    shape = new HSSFCombobox(container, objRecord);
                    break;
                }
                case 30: {
                    final EscherOptRecord optRecord = container.getChildById((short)(-4085));
                    final EscherProperty property = optRecord.lookup(325);
                    if (null != property) {
                        shape = new HSSFPolygon(container, objRecord, txtRecord);
                        break;
                    }
                    shape = new HSSFSimpleShape(container, objRecord, txtRecord);
                    break;
                }
                case 6: {
                    shape = new HSSFTextbox(container, objRecord, txtRecord);
                    break;
                }
                case 25: {
                    shape = new HSSFComment(container, objRecord, txtRecord, agg.getNoteRecordByObj(objRecord));
                    break;
                }
                default: {
                    shape = new HSSFSimpleShape(container, objRecord, txtRecord);
                    break;
                }
            }
            out.addShape(shape);
        }
    }
    
    private static boolean isEmbeddedObject(final ObjRecord obj) {
        for (final SubRecord sub : obj.getSubRecords()) {
            if (sub instanceof EmbeddedObjectRefSubRecord) {
                return true;
            }
        }
        return false;
    }
}

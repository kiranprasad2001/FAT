// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.poifs.crypt;

import java.nio.charset.Charset;
import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.StringUtil;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.poifs.filesystem.POIFSWriterEvent;
import org.apache.poi.poifs.filesystem.POIFSWriterListener;
import org.apache.poi.util.LittleEndianByteArrayOutputStream;
import org.apache.poi.poifs.filesystem.DocumentEntry;
import java.io.IOException;
import org.apache.poi.poifs.crypt.standard.EncryptionRecord;
import org.apache.poi.poifs.filesystem.DirectoryEntry;

public class DataSpaceMapUtils
{
    public static void addDefaultDataSpace(final DirectoryEntry dir) throws IOException {
        final DataSpaceMapEntry dsme = new DataSpaceMapEntry(new int[] { 0 }, new String[] { "EncryptedPackage" }, "StrongEncryptionDataSpace");
        final DataSpaceMap dsm = new DataSpaceMap(new DataSpaceMapEntry[] { dsme });
        createEncryptionEntry(dir, "\u0006DataSpaces/DataSpaceMap", dsm);
        final DataSpaceDefinition dsd = new DataSpaceDefinition(new String[] { "StrongEncryptionTransform" });
        createEncryptionEntry(dir, "\u0006DataSpaces/DataSpaceInfo/StrongEncryptionDataSpace", dsd);
        final TransformInfoHeader tih = new TransformInfoHeader(1, "{FF9A3F03-56EF-4613-BDD5-5A41C1D07246}", "Microsoft.Container.EncryptionTransform", 1, 0, 1, 0, 1, 0);
        final IRMDSTransformInfo irm = new IRMDSTransformInfo(tih, 0, null);
        createEncryptionEntry(dir, "\u0006DataSpaces/TransformInfo/StrongEncryptionTransform/\u0006Primary", irm);
        final DataSpaceVersionInfo dsvi = new DataSpaceVersionInfo("Microsoft.Container.DataSpaces", 1, 0, 1, 0, 1, 0);
        createEncryptionEntry(dir, "\u0006DataSpaces/Version", dsvi);
    }
    
    public static DocumentEntry createEncryptionEntry(DirectoryEntry dir, final String path, final EncryptionRecord out) throws IOException {
        final String[] parts = path.split("/");
        for (int i = 0; i < parts.length - 1; ++i) {
            dir = (DirectoryEntry)(dir.hasEntry(parts[i]) ? dir.getEntry(parts[i]) : dir.createDirectory(parts[i]));
        }
        final byte[] buf = new byte[5000];
        final LittleEndianByteArrayOutputStream bos = new LittleEndianByteArrayOutputStream(buf, 0);
        out.write(bos);
        final String fileName = parts[parts.length - 1];
        if (dir.hasEntry(fileName)) {
            dir.getEntry(fileName).delete();
        }
        return dir.createDocument(fileName, bos.getWriteIndex(), new POIFSWriterListener() {
            @Override
            public void processPOIFSWriterEvent(final POIFSWriterEvent event) {
                try {
                    event.getStream().write(buf, 0, event.getLimit());
                }
                catch (IOException e) {
                    throw new EncryptedDocumentException(e);
                }
            }
        });
    }
    
    public static String readUnicodeLPP4(final LittleEndianInput is) {
        final int length = is.readInt();
        if (length % 2 != 0) {
            throw new EncryptedDocumentException("UNICODE-LP-P4 structure is a multiple of 4 bytes. If Padding is present, it MUST be exactly 2 bytes long");
        }
        final String result = StringUtil.readUnicodeLE(is, length / 2);
        if (length % 4 == 2) {
            is.readShort();
        }
        return result;
    }
    
    public static void writeUnicodeLPP4(final LittleEndianOutput os, final String string) {
        final byte[] buf = StringUtil.getToUnicodeLE(string);
        os.writeInt(buf.length);
        os.write(buf);
        if (buf.length % 4 == 2) {
            os.writeShort(0);
        }
    }
    
    public static String readUtf8LPP4(final LittleEndianInput is) {
        final int length = is.readInt();
        if (length == 0 || length == 4) {
            final int skip = is.readInt();
            return (length == 0) ? null : "";
        }
        final byte[] data = new byte[length];
        is.readFully(data);
        final int scratchedBytes = length % 4;
        if (scratchedBytes > 0) {
            for (int i = 0; i < 4 - scratchedBytes; ++i) {
                is.readByte();
            }
        }
        return new String(data, 0, data.length, Charset.forName("UTF-8"));
    }
    
    public static void writeUtf8LPP4(final LittleEndianOutput os, final String str) {
        if (str == null || "".equals(str)) {
            os.writeInt((str == null) ? 0 : 4);
            os.writeInt(0);
        }
        else {
            final byte[] buf = str.getBytes(Charset.forName("UTF-8"));
            os.writeInt(buf.length);
            os.write(buf);
            final int scratchBytes = buf.length % 4;
            if (scratchBytes > 0) {
                for (int i = 0; i < 4 - scratchBytes; ++i) {
                    os.writeByte(0);
                }
            }
        }
    }
    
    public static class DataSpaceMap implements EncryptionRecord
    {
        DataSpaceMapEntry[] entries;
        
        public DataSpaceMap(final DataSpaceMapEntry[] entries) {
            this.entries = entries;
        }
        
        public DataSpaceMap(final LittleEndianInput is) {
            final int length = is.readInt();
            final int entryCount = is.readInt();
            this.entries = new DataSpaceMapEntry[entryCount];
            for (int i = 0; i < entryCount; ++i) {
                this.entries[i] = new DataSpaceMapEntry(is);
            }
        }
        
        @Override
        public void write(final LittleEndianByteArrayOutputStream os) {
            os.writeInt(8);
            os.writeInt(this.entries.length);
            for (final DataSpaceMapEntry dsme : this.entries) {
                dsme.write(os);
            }
        }
    }
    
    public static class DataSpaceMapEntry implements EncryptionRecord
    {
        int[] referenceComponentType;
        String[] referenceComponent;
        String dataSpaceName;
        
        public DataSpaceMapEntry(final int[] referenceComponentType, final String[] referenceComponent, final String dataSpaceName) {
            this.referenceComponentType = referenceComponentType;
            this.referenceComponent = referenceComponent;
            this.dataSpaceName = dataSpaceName;
        }
        
        public DataSpaceMapEntry(final LittleEndianInput is) {
            final int length = is.readInt();
            final int referenceComponentCount = is.readInt();
            this.referenceComponentType = new int[referenceComponentCount];
            this.referenceComponent = new String[referenceComponentCount];
            for (int i = 0; i < referenceComponentCount; ++i) {
                this.referenceComponentType[i] = is.readInt();
                this.referenceComponent[i] = DataSpaceMapUtils.readUnicodeLPP4(is);
            }
            this.dataSpaceName = DataSpaceMapUtils.readUnicodeLPP4(is);
        }
        
        @Override
        public void write(final LittleEndianByteArrayOutputStream os) {
            final int start = os.getWriteIndex();
            final LittleEndianOutput sizeOut = os.createDelayedOutput(4);
            os.writeInt(this.referenceComponent.length);
            for (int i = 0; i < this.referenceComponent.length; ++i) {
                os.writeInt(this.referenceComponentType[i]);
                DataSpaceMapUtils.writeUnicodeLPP4(os, this.referenceComponent[i]);
            }
            DataSpaceMapUtils.writeUnicodeLPP4(os, this.dataSpaceName);
            sizeOut.writeInt(os.getWriteIndex() - start);
        }
    }
    
    public static class DataSpaceDefinition implements EncryptionRecord
    {
        String[] transformer;
        
        public DataSpaceDefinition(final String[] transformer) {
            this.transformer = transformer;
        }
        
        public DataSpaceDefinition(final LittleEndianInput is) {
            final int headerLength = is.readInt();
            final int transformReferenceCount = is.readInt();
            this.transformer = new String[transformReferenceCount];
            for (int i = 0; i < transformReferenceCount; ++i) {
                this.transformer[i] = DataSpaceMapUtils.readUnicodeLPP4(is);
            }
        }
        
        @Override
        public void write(final LittleEndianByteArrayOutputStream bos) {
            bos.writeInt(8);
            bos.writeInt(this.transformer.length);
            for (final String str : this.transformer) {
                DataSpaceMapUtils.writeUnicodeLPP4(bos, str);
            }
        }
    }
    
    public static class IRMDSTransformInfo implements EncryptionRecord
    {
        TransformInfoHeader transformInfoHeader;
        int extensibilityHeader;
        String xrMLLicense;
        
        public IRMDSTransformInfo(final TransformInfoHeader transformInfoHeader, final int extensibilityHeader, final String xrMLLicense) {
            this.transformInfoHeader = transformInfoHeader;
            this.extensibilityHeader = extensibilityHeader;
            this.xrMLLicense = xrMLLicense;
        }
        
        public IRMDSTransformInfo(final LittleEndianInput is) {
            this.transformInfoHeader = new TransformInfoHeader(is);
            this.extensibilityHeader = is.readInt();
            this.xrMLLicense = DataSpaceMapUtils.readUtf8LPP4(is);
        }
        
        @Override
        public void write(final LittleEndianByteArrayOutputStream bos) {
            this.transformInfoHeader.write(bos);
            bos.writeInt(this.extensibilityHeader);
            DataSpaceMapUtils.writeUtf8LPP4(bos, this.xrMLLicense);
            bos.writeInt(4);
        }
    }
    
    public static class TransformInfoHeader implements EncryptionRecord
    {
        int transformType;
        String transformerId;
        String transformerName;
        int readerVersionMajor;
        int readerVersionMinor;
        int updaterVersionMajor;
        int updaterVersionMinor;
        int writerVersionMajor;
        int writerVersionMinor;
        
        public TransformInfoHeader(final int transformType, final String transformerId, final String transformerName, final int readerVersionMajor, final int readerVersionMinor, final int updaterVersionMajor, final int updaterVersionMinor, final int writerVersionMajor, final int writerVersionMinor) {
            this.readerVersionMajor = 1;
            this.readerVersionMinor = 0;
            this.updaterVersionMajor = 1;
            this.updaterVersionMinor = 0;
            this.writerVersionMajor = 1;
            this.writerVersionMinor = 0;
            this.transformType = transformType;
            this.transformerId = transformerId;
            this.transformerName = transformerName;
            this.readerVersionMajor = readerVersionMajor;
            this.readerVersionMinor = readerVersionMinor;
            this.updaterVersionMajor = updaterVersionMajor;
            this.updaterVersionMinor = updaterVersionMinor;
            this.writerVersionMajor = writerVersionMajor;
            this.writerVersionMinor = writerVersionMinor;
        }
        
        public TransformInfoHeader(final LittleEndianInput is) {
            this.readerVersionMajor = 1;
            this.readerVersionMinor = 0;
            this.updaterVersionMajor = 1;
            this.updaterVersionMinor = 0;
            this.writerVersionMajor = 1;
            this.writerVersionMinor = 0;
            final int length = is.readInt();
            this.transformType = is.readInt();
            this.transformerId = DataSpaceMapUtils.readUnicodeLPP4(is);
            this.transformerName = DataSpaceMapUtils.readUnicodeLPP4(is);
            this.readerVersionMajor = is.readShort();
            this.readerVersionMinor = is.readShort();
            this.updaterVersionMajor = is.readShort();
            this.updaterVersionMinor = is.readShort();
            this.writerVersionMajor = is.readShort();
            this.writerVersionMinor = is.readShort();
        }
        
        @Override
        public void write(final LittleEndianByteArrayOutputStream bos) {
            final int start = bos.getWriteIndex();
            final LittleEndianOutput sizeOut = bos.createDelayedOutput(4);
            bos.writeInt(this.transformType);
            DataSpaceMapUtils.writeUnicodeLPP4(bos, this.transformerId);
            sizeOut.writeInt(bos.getWriteIndex() - start);
            DataSpaceMapUtils.writeUnicodeLPP4(bos, this.transformerName);
            bos.writeShort(this.readerVersionMajor);
            bos.writeShort(this.readerVersionMinor);
            bos.writeShort(this.updaterVersionMajor);
            bos.writeShort(this.updaterVersionMinor);
            bos.writeShort(this.writerVersionMajor);
            bos.writeShort(this.writerVersionMinor);
        }
    }
    
    public static class DataSpaceVersionInfo implements EncryptionRecord
    {
        String featureIdentifier;
        int readerVersionMajor;
        int readerVersionMinor;
        int updaterVersionMajor;
        int updaterVersionMinor;
        int writerVersionMajor;
        int writerVersionMinor;
        
        public DataSpaceVersionInfo(final LittleEndianInput is) {
            this.readerVersionMajor = 1;
            this.readerVersionMinor = 0;
            this.updaterVersionMajor = 1;
            this.updaterVersionMinor = 0;
            this.writerVersionMajor = 1;
            this.writerVersionMinor = 0;
            this.featureIdentifier = DataSpaceMapUtils.readUnicodeLPP4(is);
            this.readerVersionMajor = is.readShort();
            this.readerVersionMinor = is.readShort();
            this.updaterVersionMajor = is.readShort();
            this.updaterVersionMinor = is.readShort();
            this.writerVersionMajor = is.readShort();
            this.writerVersionMinor = is.readShort();
        }
        
        public DataSpaceVersionInfo(final String featureIdentifier, final int readerVersionMajor, final int readerVersionMinor, final int updaterVersionMajor, final int updaterVersionMinor, final int writerVersionMajor, final int writerVersionMinor) {
            this.readerVersionMajor = 1;
            this.readerVersionMinor = 0;
            this.updaterVersionMajor = 1;
            this.updaterVersionMinor = 0;
            this.writerVersionMajor = 1;
            this.writerVersionMinor = 0;
            this.featureIdentifier = featureIdentifier;
            this.readerVersionMajor = readerVersionMajor;
            this.readerVersionMinor = readerVersionMinor;
            this.updaterVersionMajor = updaterVersionMajor;
            this.updaterVersionMinor = updaterVersionMinor;
            this.writerVersionMajor = writerVersionMajor;
            this.writerVersionMinor = writerVersionMinor;
        }
        
        @Override
        public void write(final LittleEndianByteArrayOutputStream bos) {
            DataSpaceMapUtils.writeUnicodeLPP4(bos, this.featureIdentifier);
            bos.writeShort(this.readerVersionMajor);
            bos.writeShort(this.readerVersionMinor);
            bos.writeShort(this.updaterVersionMajor);
            bos.writeShort(this.updaterVersionMinor);
            bos.writeShort(this.writerVersionMajor);
            bos.writeShort(this.writerVersionMinor);
        }
    }
}

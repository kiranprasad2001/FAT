// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.poifs.filesystem;

import java.io.ByteArrayOutputStream;
import org.apache.poi.util.LittleEndianOutputStream;
import java.io.OutputStream;
import org.apache.poi.util.StringUtil;
import org.apache.poi.util.LittleEndian;
import java.io.IOException;

public class Ole10Native
{
    public static final String OLE10_NATIVE = "\u0001Ole10Native";
    protected static final String ISO1 = "ISO-8859-1";
    private int totalSize;
    private short flags1;
    private String label;
    private String fileName;
    private short flags2;
    private short unknown1;
    private String command;
    private byte[] dataBuffer;
    private short flags3;
    private EncodingMode mode;
    
    public static Ole10Native createFromEmbeddedOleObject(final POIFSFileSystem poifs) throws IOException, Ole10NativeException {
        return createFromEmbeddedOleObject(poifs.getRoot());
    }
    
    public static Ole10Native createFromEmbeddedOleObject(final DirectoryNode directory) throws IOException, Ole10NativeException {
        final DocumentEntry nativeEntry = (DocumentEntry)directory.getEntry("\u0001Ole10Native");
        final byte[] data = new byte[nativeEntry.getSize()];
        final int readBytes = directory.createDocumentInputStream(nativeEntry).read(data);
        assert readBytes == data.length;
        return new Ole10Native(data, 0);
    }
    
    public Ole10Native(final String label, final String filename, final String command, final byte[] data) {
        this.flags1 = 2;
        this.flags2 = 0;
        this.unknown1 = 3;
        this.flags3 = 0;
        this.setLabel(label);
        this.setFileName(filename);
        this.setCommand(command);
        this.setDataBuffer(data);
        this.mode = EncodingMode.parsed;
    }
    
    @Deprecated
    public Ole10Native(final byte[] data, final int offset, final boolean plain) throws Ole10NativeException {
        this(data, offset);
    }
    
    public Ole10Native(final byte[] data, final int offset) throws Ole10NativeException {
        this.flags1 = 2;
        this.flags2 = 0;
        this.unknown1 = 3;
        this.flags3 = 0;
        int ofs = offset;
        if (data.length < offset + 2) {
            throw new Ole10NativeException("data is too small");
        }
        this.totalSize = LittleEndian.getInt(data, ofs);
        ofs += 4;
        this.mode = EncodingMode.unparsed;
        if (LittleEndian.getShort(data, ofs) == 2) {
            if (Character.isISOControl(data[ofs + 2])) {
                this.mode = EncodingMode.compact;
            }
            else {
                this.mode = EncodingMode.parsed;
            }
        }
        int dataSize = 0;
        switch (this.mode) {
            case parsed: {
                this.flags1 = LittleEndian.getShort(data, ofs);
                ofs += 2;
                int len = getStringLength(data, ofs);
                this.label = StringUtil.getFromCompressedUnicode(data, ofs, len - 1);
                ofs += len;
                len = getStringLength(data, ofs);
                this.fileName = StringUtil.getFromCompressedUnicode(data, ofs, len - 1);
                ofs += len;
                this.flags2 = LittleEndian.getShort(data, ofs);
                ofs += 2;
                this.unknown1 = LittleEndian.getShort(data, ofs);
                ofs += 2;
                len = LittleEndian.getInt(data, ofs);
                ofs += 4;
                this.command = StringUtil.getFromCompressedUnicode(data, ofs, len - 1);
                ofs += len;
                if (this.totalSize < ofs) {
                    throw new Ole10NativeException("Invalid Ole10Native");
                }
                dataSize = LittleEndian.getInt(data, ofs);
                ofs += 4;
                if (dataSize < 0 || this.totalSize - (ofs - 4) < dataSize) {
                    throw new Ole10NativeException("Invalid Ole10Native");
                }
                break;
            }
            case compact: {
                this.flags1 = LittleEndian.getShort(data, ofs);
                ofs += 2;
                dataSize = this.totalSize - 2;
                break;
            }
            default: {
                dataSize = this.totalSize;
                break;
            }
        }
        System.arraycopy(data, ofs, this.dataBuffer = new byte[dataSize], 0, dataSize);
        ofs += dataSize;
    }
    
    private static int getStringLength(final byte[] data, final int ofs) {
        int len;
        for (len = 0; len + ofs < data.length && data[ofs + len] != 0; ++len) {}
        return ++len;
    }
    
    public int getTotalSize() {
        return this.totalSize;
    }
    
    public short getFlags1() {
        return this.flags1;
    }
    
    public String getLabel() {
        return this.label;
    }
    
    public String getFileName() {
        return this.fileName;
    }
    
    public short getFlags2() {
        return this.flags2;
    }
    
    public short getUnknown1() {
        return this.unknown1;
    }
    
    public String getCommand() {
        return this.command;
    }
    
    public int getDataSize() {
        return this.dataBuffer.length;
    }
    
    public byte[] getDataBuffer() {
        return this.dataBuffer;
    }
    
    public short getFlags3() {
        return this.flags3;
    }
    
    public void writeOut(final OutputStream out) throws IOException {
        final LittleEndianOutputStream leosOut = new LittleEndianOutputStream(out);
        switch (this.mode) {
            case parsed: {
                final ByteArrayOutputStream bos = new ByteArrayOutputStream();
                final LittleEndianOutputStream leos = new LittleEndianOutputStream(bos);
                leos.writeShort(this.getFlags1());
                leos.write(this.getLabel().getBytes("ISO-8859-1"));
                leos.write(0);
                leos.write(this.getFileName().getBytes("ISO-8859-1"));
                leos.write(0);
                leos.writeShort(this.getFlags2());
                leos.writeShort(this.getUnknown1());
                leos.writeInt(this.getCommand().length() + 1);
                leos.write(this.getCommand().getBytes("ISO-8859-1"));
                leos.write(0);
                leos.writeInt(this.getDataSize());
                leos.write(this.getDataBuffer());
                leos.writeShort(this.getFlags3());
                leos.close();
                leosOut.writeInt(bos.size());
                bos.writeTo(out);
                break;
            }
            case compact: {
                leosOut.writeInt(this.getDataSize() + 2);
                leosOut.writeShort(this.getFlags1());
                out.write(this.getDataBuffer());
                break;
            }
            default: {
                leosOut.writeInt(this.getDataSize());
                out.write(this.getDataBuffer());
                break;
            }
        }
    }
    
    public void setFlags1(final short flags1) {
        this.flags1 = flags1;
    }
    
    public void setFlags2(final short flags2) {
        this.flags2 = flags2;
    }
    
    public void setFlags3(final short flags3) {
        this.flags3 = flags3;
    }
    
    public void setLabel(final String label) {
        this.label = label;
    }
    
    public void setFileName(final String fileName) {
        this.fileName = fileName;
    }
    
    public void setCommand(final String command) {
        this.command = command;
    }
    
    public void setUnknown1(final short unknown1) {
        this.unknown1 = unknown1;
    }
    
    public void setDataBuffer(final byte[] dataBuffer) {
        this.dataBuffer = dataBuffer;
    }
    
    private enum EncodingMode
    {
        parsed, 
        unparsed, 
        compact;
    }
}

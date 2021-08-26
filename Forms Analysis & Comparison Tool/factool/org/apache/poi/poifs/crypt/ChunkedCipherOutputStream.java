// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.poifs.crypt;

import org.apache.poi.EncryptedDocumentException;
import java.io.FileInputStream;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.poifs.filesystem.POIFSWriterEvent;
import org.apache.poi.poifs.filesystem.POIFSWriterListener;
import java.security.GeneralSecurityException;
import java.io.IOException;
import java.io.FileOutputStream;
import org.apache.poi.util.TempFile;
import java.io.OutputStream;
import javax.crypto.Cipher;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import java.io.File;
import org.apache.poi.util.Internal;
import java.io.FilterOutputStream;

@Internal
public abstract class ChunkedCipherOutputStream extends FilterOutputStream
{
    protected final int chunkSize;
    protected final int chunkMask;
    protected final int chunkBits;
    private final byte[] _chunk;
    private final File fileOut;
    private final DirectoryNode dir;
    private long _pos;
    private Cipher _cipher;
    
    public ChunkedCipherOutputStream(final DirectoryNode dir, final int chunkSize) throws IOException, GeneralSecurityException {
        super(null);
        this._pos = 0L;
        this.chunkSize = chunkSize;
        this.chunkMask = chunkSize - 1;
        this.chunkBits = Integer.bitCount(this.chunkMask);
        this._chunk = new byte[chunkSize];
        (this.fileOut = TempFile.createTempFile("encrypted_package", "crypt")).deleteOnExit();
        this.out = new FileOutputStream(this.fileOut);
        this.dir = dir;
        this._cipher = this.initCipherForBlock(null, 0, false);
    }
    
    protected abstract Cipher initCipherForBlock(final Cipher p0, final int p1, final boolean p2) throws GeneralSecurityException;
    
    protected abstract void calculateChecksum(final File p0, final int p1) throws GeneralSecurityException, IOException;
    
    protected abstract void createEncryptionInfoEntry(final DirectoryNode p0, final File p1) throws IOException, GeneralSecurityException;
    
    @Override
    public void write(final int b) throws IOException {
        this.write(new byte[] { (byte)b });
    }
    
    @Override
    public void write(final byte[] b) throws IOException {
        this.write(b, 0, b.length);
    }
    
    @Override
    public void write(final byte[] b, int off, int len) throws IOException {
        if (len == 0) {
            return;
        }
        if (len < 0 || b.length < off + len) {
            throw new IOException("not enough bytes in your input buffer");
        }
        while (len > 0) {
            final int posInChunk = (int)(this._pos & (long)this.chunkMask);
            final int nextLen = Math.min(this.chunkSize - posInChunk, len);
            System.arraycopy(b, off, this._chunk, posInChunk, nextLen);
            this._pos += nextLen;
            off += nextLen;
            len -= nextLen;
            if ((this._pos & (long)this.chunkMask) == 0x0L) {
                try {
                    this.writeChunk();
                }
                catch (GeneralSecurityException e) {
                    throw new IOException(e);
                }
            }
        }
    }
    
    protected void writeChunk() throws IOException, GeneralSecurityException {
        int posInChunk = (int)(this._pos & (long)this.chunkMask);
        int index = (int)(this._pos >> this.chunkBits);
        boolean lastChunk;
        if (posInChunk == 0) {
            --index;
            posInChunk = this.chunkSize;
            lastChunk = false;
        }
        else {
            lastChunk = true;
        }
        this._cipher = this.initCipherForBlock(this._cipher, index, lastChunk);
        final int ciLen = this._cipher.doFinal(this._chunk, 0, posInChunk, this._chunk);
        this.out.write(this._chunk, 0, ciLen);
    }
    
    @Override
    public void close() throws IOException {
        try {
            this.writeChunk();
            super.close();
            final int oleStreamSize = (int)(this.fileOut.length() + 8L);
            this.calculateChecksum(this.fileOut, oleStreamSize);
            this.dir.createDocument("EncryptedPackage", oleStreamSize, new EncryptedPackageWriter());
            this.createEncryptionInfoEntry(this.dir, this.fileOut);
        }
        catch (GeneralSecurityException e) {
            throw new IOException(e);
        }
    }
    
    private class EncryptedPackageWriter implements POIFSWriterListener
    {
        @Override
        public void processPOIFSWriterEvent(final POIFSWriterEvent event) {
            try {
                final OutputStream os = event.getStream();
                final byte[] buf = new byte[ChunkedCipherOutputStream.this.chunkSize];
                LittleEndian.putLong(buf, 0, ChunkedCipherOutputStream.this._pos);
                os.write(buf, 0, 8);
                final FileInputStream fis = new FileInputStream(ChunkedCipherOutputStream.this.fileOut);
                int readBytes;
                while ((readBytes = fis.read(buf)) != -1) {
                    os.write(buf, 0, readBytes);
                }
                fis.close();
                os.close();
                ChunkedCipherOutputStream.this.fileOut.delete();
            }
            catch (IOException e) {
                throw new EncryptedDocumentException(e);
            }
        }
    }
}

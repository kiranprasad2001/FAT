// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.poifs.crypt;

import org.apache.poi.EncryptedDocumentException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.io.InputStream;
import org.apache.poi.util.LittleEndianInput;
import javax.crypto.Cipher;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndianInputStream;

@Internal
public abstract class ChunkedCipherInputStream extends LittleEndianInputStream
{
    private final int chunkSize;
    private final int chunkMask;
    private final int chunkBits;
    private int _lastIndex;
    private long _pos;
    private long _size;
    private byte[] _chunk;
    private Cipher _cipher;
    
    public ChunkedCipherInputStream(final LittleEndianInput stream, final long size, final int chunkSize) throws GeneralSecurityException {
        super((InputStream)stream);
        this._lastIndex = 0;
        this._pos = 0L;
        this._size = size;
        this.chunkSize = chunkSize;
        this.chunkMask = chunkSize - 1;
        this.chunkBits = Integer.bitCount(this.chunkMask);
        this._cipher = this.initCipherForBlock(null, 0);
    }
    
    protected abstract Cipher initCipherForBlock(final Cipher p0, final int p1) throws GeneralSecurityException;
    
    @Override
    public int read() throws IOException {
        final byte[] b = { 0 };
        if (this.read(b) == 1) {
            return b[0];
        }
        return -1;
    }
    
    @Override
    public int read(final byte[] b, int off, int len) throws IOException {
        int total = 0;
        if (this.available() <= 0) {
            return -1;
        }
        while (len > 0) {
            if (this._chunk == null) {
                try {
                    this._chunk = this.nextChunk();
                }
                catch (GeneralSecurityException e) {
                    throw new EncryptedDocumentException(e.getMessage(), e);
                }
            }
            int count = (int)(this.chunkSize - (this._pos & (long)this.chunkMask));
            final int avail = this.available();
            if (avail == 0) {
                return total;
            }
            count = Math.min(avail, Math.min(count, len));
            System.arraycopy(this._chunk, (int)(this._pos & (long)this.chunkMask), b, off, count);
            off += count;
            len -= count;
            this._pos += count;
            if ((this._pos & (long)this.chunkMask) == 0x0L) {
                this._chunk = null;
            }
            total += count;
        }
        return total;
    }
    
    @Override
    public long skip(final long n) throws IOException {
        final long start = this._pos;
        final long skip = Math.min(this.available(), n);
        if (((this._pos + skip ^ start) & (long)~this.chunkMask) != 0x0L) {
            this._chunk = null;
        }
        this._pos += skip;
        return skip;
    }
    
    @Override
    public int available() {
        return (int)(this._size - this._pos);
    }
    
    @Override
    public boolean markSupported() {
        return false;
    }
    
    @Override
    public synchronized void mark(final int readlimit) {
        throw new UnsupportedOperationException();
    }
    
    @Override
    public synchronized void reset() throws IOException {
        throw new UnsupportedOperationException();
    }
    
    private byte[] nextChunk() throws GeneralSecurityException, IOException {
        final int index = (int)(this._pos >> this.chunkBits);
        this.initCipherForBlock(this._cipher, index);
        if (this._lastIndex != index) {
            super.skip(index - this._lastIndex << this.chunkBits);
        }
        final byte[] block = new byte[Math.min(super.available(), this.chunkSize)];
        super.read(block, 0, block.length);
        this._lastIndex = index + 1;
        return this._cipher.doFinal(block);
    }
}

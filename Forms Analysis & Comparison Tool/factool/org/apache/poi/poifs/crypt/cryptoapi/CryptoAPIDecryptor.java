// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.poifs.crypt.cryptoapi;

import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.util.BitField;
import javax.crypto.ShortBufferException;
import java.io.IOException;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import java.io.ByteArrayInputStream;
import org.apache.poi.util.BoundedInputStream;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.util.LittleEndianInputStream;
import java.io.OutputStream;
import org.apache.poi.util.IOUtils;
import java.io.ByteArrayOutputStream;
import org.apache.poi.poifs.filesystem.Entry;
import org.apache.poi.poifs.filesystem.DocumentNode;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import java.io.InputStream;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.util.StringUtil;
import org.apache.poi.poifs.crypt.EncryptionHeader;
import java.security.Key;
import org.apache.poi.poifs.crypt.ChainingMode;
import javax.crypto.spec.SecretKeySpec;
import org.apache.poi.util.LittleEndian;
import java.security.MessageDigest;
import org.apache.poi.poifs.crypt.HashAlgorithm;
import javax.crypto.SecretKey;
import org.apache.poi.poifs.crypt.EncryptionVerifier;
import java.security.GeneralSecurityException;
import org.apache.poi.EncryptedDocumentException;
import java.util.Arrays;
import org.apache.poi.poifs.crypt.CryptoFunctions;
import javax.crypto.Cipher;
import org.apache.poi.poifs.crypt.EncryptionInfoBuilder;
import org.apache.poi.poifs.crypt.Decryptor;

public class CryptoAPIDecryptor extends Decryptor
{
    private long _length;
    
    protected CryptoAPIDecryptor(final CryptoAPIEncryptionInfoBuilder builder) {
        super(builder);
        this._length = -1L;
    }
    
    @Override
    public boolean verifyPassword(final String password) {
        final EncryptionVerifier ver = this.builder.getVerifier();
        final SecretKey skey = generateSecretKey(password, ver);
        try {
            final Cipher cipher = initCipherForBlock(null, 0, this.builder, skey, 2);
            final byte[] encryptedVerifier = ver.getEncryptedVerifier();
            final byte[] verifier = new byte[encryptedVerifier.length];
            cipher.update(encryptedVerifier, 0, encryptedVerifier.length, verifier);
            this.setVerifier(verifier);
            final byte[] encryptedVerifierHash = ver.getEncryptedVerifierHash();
            final byte[] verifierHash = cipher.doFinal(encryptedVerifierHash);
            final HashAlgorithm hashAlgo = ver.getHashAlgorithm();
            final MessageDigest hashAlg = CryptoFunctions.getMessageDigest(hashAlgo);
            final byte[] calcVerifierHash = hashAlg.digest(verifier);
            if (Arrays.equals(calcVerifierHash, verifierHash)) {
                this.setSecretKey(skey);
                return true;
            }
        }
        catch (GeneralSecurityException e) {
            throw new EncryptedDocumentException(e);
        }
        return false;
    }
    
    public Cipher initCipherForBlock(final Cipher cipher, final int block) throws GeneralSecurityException {
        return initCipherForBlock(cipher, block, this.builder, this.getSecretKey(), 2);
    }
    
    protected static Cipher initCipherForBlock(Cipher cipher, final int block, final EncryptionInfoBuilder builder, final SecretKey skey, final int encryptMode) throws GeneralSecurityException {
        final EncryptionVerifier ver = builder.getVerifier();
        final HashAlgorithm hashAlgo = ver.getHashAlgorithm();
        final byte[] blockKey = new byte[4];
        LittleEndian.putUInt(blockKey, 0, block);
        final MessageDigest hashAlg = CryptoFunctions.getMessageDigest(hashAlgo);
        hashAlg.update(skey.getEncoded());
        byte[] encKey = hashAlg.digest(blockKey);
        final EncryptionHeader header = builder.getHeader();
        final int keyBits = header.getKeySize();
        encKey = CryptoFunctions.getBlock0(encKey, keyBits / 8);
        if (keyBits == 40) {
            encKey = CryptoFunctions.getBlock0(encKey, 16);
        }
        final SecretKey key = new SecretKeySpec(encKey, skey.getAlgorithm());
        if (cipher == null) {
            cipher = CryptoFunctions.getCipher(key, header.getCipherAlgorithm(), null, null, encryptMode);
        }
        else {
            cipher.init(encryptMode, key);
        }
        return cipher;
    }
    
    protected static SecretKey generateSecretKey(String password, final EncryptionVerifier ver) {
        if (password.length() > 255) {
            password = password.substring(0, 255);
        }
        final HashAlgorithm hashAlgo = ver.getHashAlgorithm();
        final MessageDigest hashAlg = CryptoFunctions.getMessageDigest(hashAlgo);
        hashAlg.update(ver.getSalt());
        final byte[] hash = hashAlg.digest(StringUtil.getToUnicodeLE(password));
        final SecretKey skey = new SecretKeySpec(hash, ver.getCipherAlgorithm().jceId);
        return skey;
    }
    
    @Override
    public InputStream getDataStream(final DirectoryNode dir) throws IOException, GeneralSecurityException {
        final POIFSFileSystem fsOut = new POIFSFileSystem();
        final DocumentNode es = (DocumentNode)dir.getEntry("EncryptedSummary");
        final DocumentInputStream dis = dir.createDocumentInputStream(es);
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        IOUtils.copy(dis, bos);
        dis.close();
        SeekableByteArrayInputStream sbis = new SeekableByteArrayInputStream(bos.toByteArray());
        final LittleEndianInputStream leis = new LittleEndianInputStream(sbis);
        final int streamDescriptorArrayOffset = (int)leis.readUInt();
        final int streamDescriptorArraySize = (int)leis.readUInt();
        sbis.skip(streamDescriptorArrayOffset - 8);
        sbis.setBlock(0);
        final int encryptedStreamDescriptorCount = (int)leis.readUInt();
        final StreamDescriptorEntry[] entries = new StreamDescriptorEntry[encryptedStreamDescriptorCount];
        for (int i = 0; i < encryptedStreamDescriptorCount; ++i) {
            final StreamDescriptorEntry entry = new StreamDescriptorEntry();
            entries[i] = entry;
            entry.streamOffset = (int)leis.readUInt();
            entry.streamSize = (int)leis.readUInt();
            entry.block = leis.readUShort();
            final int nameSize = leis.readUByte();
            entry.flags = leis.readUByte();
            final boolean isStream = StreamDescriptorEntry.flagStream.isSet(entry.flags);
            entry.reserved2 = leis.readInt();
            entry.streamName = StringUtil.readUnicodeLE(leis, nameSize);
            leis.readShort();
            assert entry.streamName.length() == nameSize;
        }
        for (final StreamDescriptorEntry entry2 : entries) {
            sbis.seek(entry2.streamOffset);
            sbis.setBlock(entry2.block);
            final InputStream is = new BoundedInputStream(sbis, entry2.streamSize);
            fsOut.createDocument(is, entry2.streamName);
        }
        leis.close();
        sbis = null;
        bos.reset();
        fsOut.writeFilesystem(bos);
        this._length = bos.size();
        final ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        return bis;
    }
    
    @Override
    public long getLength() {
        if (this._length == -1L) {
            throw new IllegalStateException("Decryptor.getDataStream() was not called");
        }
        return this._length;
    }
    
    private class SeekableByteArrayInputStream extends ByteArrayInputStream
    {
        Cipher cipher;
        byte[] oneByte;
        
        public void seek(final int pos) {
            if (pos > this.count) {
                throw new ArrayIndexOutOfBoundsException(pos);
            }
            this.pos = pos;
            this.mark = pos;
        }
        
        public void setBlock(final int block) throws GeneralSecurityException {
            this.cipher = CryptoAPIDecryptor.this.initCipherForBlock(this.cipher, block);
        }
        
        @Override
        public synchronized int read() {
            final int ch = super.read();
            if (ch == -1) {
                return -1;
            }
            this.oneByte[0] = (byte)ch;
            try {
                this.cipher.update(this.oneByte, 0, 1, this.oneByte);
            }
            catch (ShortBufferException e) {
                throw new EncryptedDocumentException(e);
            }
            return this.oneByte[0];
        }
        
        @Override
        public synchronized int read(final byte[] b, final int off, final int len) {
            final int readLen = super.read(b, off, len);
            if (readLen == -1) {
                return -1;
            }
            try {
                this.cipher.update(b, off, readLen, b, off);
            }
            catch (ShortBufferException e) {
                throw new EncryptedDocumentException(e);
            }
            return readLen;
        }
        
        public SeekableByteArrayInputStream(final byte[] buf) throws GeneralSecurityException {
            super(buf);
            this.oneByte = new byte[] { 0 };
            this.cipher = CryptoAPIDecryptor.this.initCipherForBlock(null, 0);
        }
    }
    
    static class StreamDescriptorEntry
    {
        static BitField flagStream;
        int streamOffset;
        int streamSize;
        int block;
        int flags;
        int reserved2;
        String streamName;
        
        static {
            StreamDescriptorEntry.flagStream = BitFieldFactory.getInstance(1);
        }
    }
}

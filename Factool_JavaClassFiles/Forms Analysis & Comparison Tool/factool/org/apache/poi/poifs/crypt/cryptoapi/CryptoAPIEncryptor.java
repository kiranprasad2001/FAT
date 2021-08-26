// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.poifs.crypt.cryptoapi;

import java.io.ByteArrayOutputStream;
import org.apache.poi.util.LittleEndianByteArrayOutputStream;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.crypt.standard.EncryptionRecord;
import org.apache.poi.poifs.crypt.DataSpaceMapUtils;
import org.apache.poi.hpsf.DocumentSummaryInformation;
import java.util.Iterator;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import java.util.List;
import org.apache.poi.hpsf.WritingNotSupportedException;
import java.io.IOException;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.hpsf.PropertySetFactory;
import java.io.ByteArrayInputStream;
import org.apache.poi.util.StringUtil;
import org.apache.poi.util.LittleEndian;
import java.io.InputStream;
import org.apache.poi.util.IOUtils;
import java.util.ArrayList;
import java.io.OutputStream;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.poifs.crypt.EncryptionInfoBuilder;
import java.security.MessageDigest;
import org.apache.poi.poifs.crypt.HashAlgorithm;
import javax.crypto.SecretKey;
import java.security.GeneralSecurityException;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.poifs.crypt.CryptoFunctions;
import javax.crypto.Cipher;
import org.apache.poi.poifs.crypt.EncryptionVerifier;
import java.util.Random;
import java.security.SecureRandom;
import org.apache.poi.poifs.crypt.Encryptor;

public class CryptoAPIEncryptor extends Encryptor
{
    private final CryptoAPIEncryptionInfoBuilder builder;
    
    protected CryptoAPIEncryptor(final CryptoAPIEncryptionInfoBuilder builder) {
        this.builder = builder;
    }
    
    @Override
    public void confirmPassword(final String password) {
        final Random r = new SecureRandom();
        final byte[] salt = new byte[16];
        final byte[] verifier = new byte[16];
        r.nextBytes(salt);
        r.nextBytes(verifier);
        this.confirmPassword(password, null, null, verifier, salt, null);
    }
    
    @Override
    public void confirmPassword(final String password, final byte[] keySpec, final byte[] keySalt, final byte[] verifier, final byte[] verifierSalt, final byte[] integritySalt) {
        assert verifier != null && verifierSalt != null;
        final CryptoAPIEncryptionVerifier ver = this.builder.getVerifier();
        ver.setSalt(verifierSalt);
        final SecretKey skey = CryptoAPIDecryptor.generateSecretKey(password, ver);
        this.setSecretKey(skey);
        try {
            final Cipher cipher = this.initCipherForBlock(null, 0);
            final byte[] encryptedVerifier = new byte[verifier.length];
            cipher.update(verifier, 0, verifier.length, encryptedVerifier);
            ver.setEncryptedVerifier(encryptedVerifier);
            final HashAlgorithm hashAlgo = ver.getHashAlgorithm();
            final MessageDigest hashAlg = CryptoFunctions.getMessageDigest(hashAlgo);
            final byte[] calcVerifierHash = hashAlg.digest(verifier);
            final byte[] encryptedVerifierHash = cipher.doFinal(calcVerifierHash);
            ver.setEncryptedVerifierHash(encryptedVerifierHash);
        }
        catch (GeneralSecurityException e) {
            throw new EncryptedDocumentException("Password confirmation failed", e);
        }
    }
    
    public Cipher initCipherForBlock(final Cipher cipher, final int block) throws GeneralSecurityException {
        return CryptoAPIDecryptor.initCipherForBlock(cipher, block, this.builder, this.getSecretKey(), 1);
    }
    
    @Override
    public OutputStream getDataStream(final DirectoryNode dir) throws IOException, GeneralSecurityException {
        final CipherByteArrayOutputStream bos = new CipherByteArrayOutputStream();
        final byte[] buf = new byte[8];
        bos.write(buf, 0, 8);
        final String[] entryNames = { "\u0005SummaryInformation", "\u0005DocumentSummaryInformation" };
        final List<CryptoAPIDecryptor.StreamDescriptorEntry> descList = new ArrayList<CryptoAPIDecryptor.StreamDescriptorEntry>();
        int block = 0;
        for (final String entryName : entryNames) {
            if (dir.hasEntry(entryName)) {
                final CryptoAPIDecryptor.StreamDescriptorEntry descEntry = new CryptoAPIDecryptor.StreamDescriptorEntry();
                descEntry.block = block;
                descEntry.streamOffset = bos.size();
                descEntry.streamName = entryName;
                descEntry.flags = CryptoAPIDecryptor.StreamDescriptorEntry.flagStream.setValue(0, 1);
                descEntry.reserved2 = 0;
                bos.setBlock(block);
                final DocumentInputStream dis = dir.createDocumentInputStream(entryName);
                IOUtils.copy(dis, bos);
                dis.close();
                descEntry.streamSize = bos.size() - descEntry.streamOffset;
                descList.add(descEntry);
                dir.getEntry(entryName).delete();
                ++block;
            }
        }
        final int streamDescriptorArrayOffset = bos.size();
        bos.setBlock(0);
        LittleEndian.putUInt(buf, 0, descList.size());
        bos.write(buf, 0, 4);
        for (final CryptoAPIDecryptor.StreamDescriptorEntry sde : descList) {
            LittleEndian.putUInt(buf, 0, sde.streamOffset);
            bos.write(buf, 0, 4);
            LittleEndian.putUInt(buf, 0, sde.streamSize);
            bos.write(buf, 0, 4);
            LittleEndian.putUShort(buf, 0, sde.block);
            bos.write(buf, 0, 2);
            LittleEndian.putUByte(buf, 0, (short)sde.streamName.length());
            bos.write(buf, 0, 1);
            LittleEndian.putUByte(buf, 0, (short)sde.flags);
            bos.write(buf, 0, 1);
            LittleEndian.putUInt(buf, 0, sde.reserved2);
            bos.write(buf, 0, 4);
            final byte[] nameBytes = StringUtil.getToUnicodeLE(sde.streamName);
            bos.write(nameBytes, 0, nameBytes.length);
            LittleEndian.putShort(buf, 0, (short)0);
            bos.write(buf, 0, 2);
        }
        final int savedSize = bos.size();
        final int streamDescriptorArraySize = savedSize - streamDescriptorArrayOffset;
        LittleEndian.putUInt(buf, 0, streamDescriptorArrayOffset);
        LittleEndian.putUInt(buf, 4, streamDescriptorArraySize);
        bos.reset();
        bos.setBlock(0);
        bos.write(buf, 0, 8);
        bos.setSize(savedSize);
        dir.createDocument("EncryptedSummary", new ByteArrayInputStream(bos.getBuf(), 0, savedSize));
        final DocumentSummaryInformation dsi = PropertySetFactory.newDocumentSummaryInformation();
        try {
            dsi.write(dir, "\u0005DocumentSummaryInformation");
        }
        catch (WritingNotSupportedException e) {
            throw new IOException(e);
        }
        return bos;
    }
    
    protected int getKeySizeInBytes() {
        return this.builder.getHeader().getKeySize() / 8;
    }
    
    protected void createEncryptionInfoEntry(final DirectoryNode dir) throws IOException {
        DataSpaceMapUtils.addDefaultDataSpace(dir);
        final EncryptionInfo info = this.builder.getEncryptionInfo();
        final CryptoAPIEncryptionHeader header = this.builder.getHeader();
        final CryptoAPIEncryptionVerifier verifier = this.builder.getVerifier();
        final EncryptionRecord er = new EncryptionRecord() {
            @Override
            public void write(final LittleEndianByteArrayOutputStream bos) {
                bos.writeShort(info.getVersionMajor());
                bos.writeShort(info.getVersionMinor());
                header.write(bos);
                verifier.write(bos);
            }
        };
        DataSpaceMapUtils.createEncryptionEntry(dir, "EncryptionInfo", er);
    }
    
    private class CipherByteArrayOutputStream extends ByteArrayOutputStream
    {
        Cipher cipher;
        byte[] oneByte;
        
        public CipherByteArrayOutputStream() throws GeneralSecurityException {
            this.oneByte = new byte[] { 0 };
            this.setBlock(0);
        }
        
        public byte[] getBuf() {
            return this.buf;
        }
        
        public void setSize(final int count) {
            this.count = count;
        }
        
        public void setBlock(final int block) throws GeneralSecurityException {
            this.cipher = CryptoAPIEncryptor.this.initCipherForBlock(this.cipher, block);
        }
        
        @Override
        public void write(final int b) {
            try {
                this.oneByte[0] = (byte)b;
                this.cipher.update(this.oneByte, 0, 1, this.oneByte, 0);
                super.write(this.oneByte);
            }
            catch (Exception e) {
                throw new EncryptedDocumentException(e);
            }
        }
        
        @Override
        public void write(final byte[] b, final int off, final int len) {
            try {
                this.cipher.update(b, off, len, b, off);
                super.write(b, off, len);
            }
            catch (Exception e) {
                throw new EncryptedDocumentException(e);
            }
        }
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.poifs.crypt.binaryrc4;

import java.io.File;
import org.apache.poi.poifs.crypt.ChunkedCipherOutputStream;
import org.apache.poi.util.LittleEndianByteArrayOutputStream;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.crypt.standard.EncryptionRecord;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.crypt.DataSpaceMapUtils;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import java.security.MessageDigest;
import org.apache.poi.poifs.crypt.HashAlgorithm;
import javax.crypto.SecretKey;
import java.security.GeneralSecurityException;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.poifs.crypt.CryptoFunctions;
import org.apache.poi.poifs.crypt.EncryptionInfoBuilder;
import javax.crypto.Cipher;
import org.apache.poi.poifs.crypt.EncryptionVerifier;
import java.util.Random;
import java.security.SecureRandom;
import org.apache.poi.poifs.crypt.Encryptor;

public class BinaryRC4Encryptor extends Encryptor
{
    private final BinaryRC4EncryptionInfoBuilder builder;
    
    protected BinaryRC4Encryptor(final BinaryRC4EncryptionInfoBuilder builder) {
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
        final BinaryRC4EncryptionVerifier ver = this.builder.getVerifier();
        ver.setSalt(verifierSalt);
        final SecretKey skey = BinaryRC4Decryptor.generateSecretKey(password, ver);
        this.setSecretKey(skey);
        try {
            final Cipher cipher = BinaryRC4Decryptor.initCipherForBlock(null, 0, this.builder, skey, 1);
            final byte[] encryptedVerifier = new byte[16];
            cipher.update(verifier, 0, 16, encryptedVerifier);
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
    
    @Override
    public OutputStream getDataStream(final DirectoryNode dir) throws IOException, GeneralSecurityException {
        final OutputStream countStream = new BinaryRC4CipherOutputStream(dir);
        return countStream;
    }
    
    protected int getKeySizeInBytes() {
        return this.builder.getHeader().getKeySize() / 8;
    }
    
    protected void createEncryptionInfoEntry(final DirectoryNode dir) throws IOException {
        DataSpaceMapUtils.addDefaultDataSpace(dir);
        final EncryptionInfo info = this.builder.getEncryptionInfo();
        final BinaryRC4EncryptionHeader header = this.builder.getHeader();
        final BinaryRC4EncryptionVerifier verifier = this.builder.getVerifier();
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
    
    protected class BinaryRC4CipherOutputStream extends ChunkedCipherOutputStream
    {
        @Override
        protected Cipher initCipherForBlock(final Cipher cipher, final int block, final boolean lastChunk) throws GeneralSecurityException {
            return BinaryRC4Decryptor.initCipherForBlock(cipher, block, BinaryRC4Encryptor.this.builder, BinaryRC4Encryptor.this.getSecretKey(), 1);
        }
        
        @Override
        protected void calculateChecksum(final File file, final int i) {
        }
        
        @Override
        protected void createEncryptionInfoEntry(final DirectoryNode dir, final File tmpFile) throws IOException, GeneralSecurityException {
            BinaryRC4Encryptor.this.createEncryptionInfoEntry(dir);
        }
        
        public BinaryRC4CipherOutputStream(final DirectoryNode dir) throws IOException, GeneralSecurityException {
            super(dir, 512);
        }
    }
}

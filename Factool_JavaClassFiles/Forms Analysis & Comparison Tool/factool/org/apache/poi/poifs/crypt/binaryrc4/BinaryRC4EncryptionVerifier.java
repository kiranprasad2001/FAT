// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.poifs.crypt.binaryrc4;

import org.apache.poi.util.LittleEndianByteArrayOutputStream;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.poifs.crypt.HashAlgorithm;
import org.apache.poi.poifs.crypt.ChainingMode;
import org.apache.poi.poifs.crypt.CipherAlgorithm;
import org.apache.poi.poifs.crypt.standard.EncryptionRecord;
import org.apache.poi.poifs.crypt.EncryptionVerifier;

public class BinaryRC4EncryptionVerifier extends EncryptionVerifier implements EncryptionRecord
{
    protected BinaryRC4EncryptionVerifier() {
        this.setSpinCount(-1);
        this.setCipherAlgorithm(CipherAlgorithm.rc4);
        this.setChainingMode(null);
        this.setEncryptedKey(null);
        this.setHashAlgorithm(HashAlgorithm.md5);
    }
    
    protected BinaryRC4EncryptionVerifier(final LittleEndianInput is) {
        final byte[] salt = new byte[16];
        is.readFully(salt);
        this.setSalt(salt);
        final byte[] encryptedVerifier = new byte[16];
        is.readFully(encryptedVerifier);
        this.setEncryptedVerifier(encryptedVerifier);
        final byte[] encryptedVerifierHash = new byte[16];
        is.readFully(encryptedVerifierHash);
        this.setEncryptedVerifierHash(encryptedVerifierHash);
        this.setSpinCount(-1);
        this.setCipherAlgorithm(CipherAlgorithm.rc4);
        this.setChainingMode(null);
        this.setEncryptedKey(null);
        this.setHashAlgorithm(HashAlgorithm.md5);
    }
    
    @Override
    protected void setSalt(final byte[] salt) {
        if (salt == null || salt.length != 16) {
            throw new EncryptedDocumentException("invalid verifier salt");
        }
        super.setSalt(salt);
    }
    
    @Override
    protected void setEncryptedVerifier(final byte[] encryptedVerifier) {
        super.setEncryptedVerifier(encryptedVerifier);
    }
    
    @Override
    protected void setEncryptedVerifierHash(final byte[] encryptedVerifierHash) {
        super.setEncryptedVerifierHash(encryptedVerifierHash);
    }
    
    @Override
    public void write(final LittleEndianByteArrayOutputStream bos) {
        final byte[] salt = this.getSalt();
        assert salt.length == 16;
        bos.write(salt);
        final byte[] encryptedVerifier = this.getEncryptedVerifier();
        assert encryptedVerifier.length == 16;
        bos.write(encryptedVerifier);
        final byte[] encryptedVerifierHash = this.getEncryptedVerifierHash();
        assert encryptedVerifierHash.length == 16;
        bos.write(encryptedVerifierHash);
    }
}

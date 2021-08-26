// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.poifs.crypt.standard;

import org.apache.poi.util.LittleEndianByteArrayOutputStream;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.poifs.crypt.ChainingMode;
import org.apache.poi.poifs.crypt.HashAlgorithm;
import org.apache.poi.poifs.crypt.CipherAlgorithm;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.poifs.crypt.EncryptionVerifier;

public class StandardEncryptionVerifier extends EncryptionVerifier implements EncryptionRecord
{
    private static final int SPIN_COUNT = 50000;
    private final int verifierHashSize;
    
    protected StandardEncryptionVerifier(final LittleEndianInput is, final StandardEncryptionHeader header) {
        final int saltSize = is.readInt();
        if (saltSize != 16) {
            throw new RuntimeException("Salt size != 16 !?");
        }
        final byte[] salt = new byte[16];
        is.readFully(salt);
        this.setSalt(salt);
        final byte[] encryptedVerifier = new byte[16];
        is.readFully(encryptedVerifier);
        this.setEncryptedVerifier(encryptedVerifier);
        this.verifierHashSize = is.readInt();
        final byte[] encryptedVerifierHash = new byte[header.getCipherAlgorithm().encryptedVerifierHashLength];
        is.readFully(encryptedVerifierHash);
        this.setEncryptedVerifierHash(encryptedVerifierHash);
        this.setSpinCount(50000);
        this.setCipherAlgorithm(header.getCipherAlgorithm());
        this.setChainingMode(header.getChainingMode());
        this.setEncryptedKey(null);
        this.setHashAlgorithm(header.getHashAlgorithmEx());
    }
    
    protected StandardEncryptionVerifier(final CipherAlgorithm cipherAlgorithm, final HashAlgorithm hashAlgorithm, final int keyBits, final int blockSize, final ChainingMode chainingMode) {
        this.setCipherAlgorithm(cipherAlgorithm);
        this.setHashAlgorithm(hashAlgorithm);
        this.setChainingMode(chainingMode);
        this.setSpinCount(50000);
        this.verifierHashSize = hashAlgorithm.hashSize;
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
        bos.writeInt(salt.length);
        bos.write(salt);
        final byte[] encryptedVerifier = this.getEncryptedVerifier();
        assert encryptedVerifier.length == 16;
        bos.write(encryptedVerifier);
        bos.writeInt(20);
        final byte[] encryptedVerifierHash = this.getEncryptedVerifierHash();
        assert encryptedVerifierHash.length == this.getCipherAlgorithm().encryptedVerifierHashLength;
        bos.write(encryptedVerifierHash);
    }
    
    protected int getVerifierHashSize() {
        return this.verifierHashSize;
    }
}

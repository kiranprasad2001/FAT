// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.poifs.crypt.standard;

import org.apache.poi.poifs.crypt.EncryptionHeader;
import org.apache.poi.poifs.crypt.EncryptionVerifier;
import org.apache.poi.poifs.crypt.Decryptor;
import org.apache.poi.poifs.crypt.Encryptor;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.poifs.crypt.ChainingMode;
import org.apache.poi.poifs.crypt.HashAlgorithm;
import org.apache.poi.poifs.crypt.CipherAlgorithm;
import java.io.IOException;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.crypt.EncryptionInfoBuilder;

public class StandardEncryptionInfoBuilder implements EncryptionInfoBuilder
{
    EncryptionInfo info;
    StandardEncryptionHeader header;
    StandardEncryptionVerifier verifier;
    StandardDecryptor decryptor;
    StandardEncryptor encryptor;
    
    @Override
    public void initialize(final EncryptionInfo info, final LittleEndianInput dis) throws IOException {
        this.info = info;
        final int hSize = dis.readInt();
        this.header = new StandardEncryptionHeader(dis);
        this.verifier = new StandardEncryptionVerifier(dis, this.header);
        if (info.getVersionMinor() == 2 && (info.getVersionMajor() == 3 || info.getVersionMajor() == 4)) {
            this.decryptor = new StandardDecryptor(this);
        }
    }
    
    @Override
    public void initialize(final EncryptionInfo info, CipherAlgorithm cipherAlgorithm, HashAlgorithm hashAlgorithm, int keyBits, int blockSize, ChainingMode chainingMode) {
        this.info = info;
        if (cipherAlgorithm == null) {
            cipherAlgorithm = CipherAlgorithm.aes128;
        }
        if (cipherAlgorithm != CipherAlgorithm.aes128 && cipherAlgorithm != CipherAlgorithm.aes192 && cipherAlgorithm != CipherAlgorithm.aes256) {
            throw new EncryptedDocumentException("Standard encryption only supports AES128/192/256.");
        }
        if (hashAlgorithm == null) {
            hashAlgorithm = HashAlgorithm.sha1;
        }
        if (hashAlgorithm != HashAlgorithm.sha1) {
            throw new EncryptedDocumentException("Standard encryption only supports SHA-1.");
        }
        if (chainingMode == null) {
            chainingMode = ChainingMode.ecb;
        }
        if (chainingMode != ChainingMode.ecb) {
            throw new EncryptedDocumentException("Standard encryption only supports ECB chaining.");
        }
        if (keyBits == -1) {
            keyBits = cipherAlgorithm.defaultKeySize;
        }
        if (blockSize == -1) {
            blockSize = cipherAlgorithm.blockSize;
        }
        boolean found = false;
        for (final int ks : cipherAlgorithm.allowedKeySize) {
            found |= (ks == keyBits);
        }
        if (!found) {
            throw new EncryptedDocumentException("KeySize " + keyBits + " not allowed for Cipher " + cipherAlgorithm.toString());
        }
        this.header = new StandardEncryptionHeader(cipherAlgorithm, hashAlgorithm, keyBits, blockSize, chainingMode);
        this.verifier = new StandardEncryptionVerifier(cipherAlgorithm, hashAlgorithm, keyBits, blockSize, chainingMode);
        this.decryptor = new StandardDecryptor(this);
        this.encryptor = new StandardEncryptor(this);
    }
    
    @Override
    public StandardEncryptionHeader getHeader() {
        return this.header;
    }
    
    @Override
    public StandardEncryptionVerifier getVerifier() {
        return this.verifier;
    }
    
    @Override
    public StandardDecryptor getDecryptor() {
        return this.decryptor;
    }
    
    @Override
    public StandardEncryptor getEncryptor() {
        return this.encryptor;
    }
    
    public EncryptionInfo getEncryptionInfo() {
        return this.info;
    }
}

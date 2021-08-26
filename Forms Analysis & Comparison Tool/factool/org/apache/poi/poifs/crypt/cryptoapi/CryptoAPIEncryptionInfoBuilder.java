// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.poifs.crypt.cryptoapi;

import org.apache.poi.poifs.crypt.EncryptionHeader;
import org.apache.poi.poifs.crypt.EncryptionVerifier;
import org.apache.poi.poifs.crypt.Decryptor;
import org.apache.poi.poifs.crypt.Encryptor;
import org.apache.poi.poifs.crypt.ChainingMode;
import org.apache.poi.poifs.crypt.HashAlgorithm;
import org.apache.poi.poifs.crypt.CipherAlgorithm;
import java.io.IOException;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.crypt.EncryptionInfoBuilder;

public class CryptoAPIEncryptionInfoBuilder implements EncryptionInfoBuilder
{
    EncryptionInfo info;
    CryptoAPIEncryptionHeader header;
    CryptoAPIEncryptionVerifier verifier;
    CryptoAPIDecryptor decryptor;
    CryptoAPIEncryptor encryptor;
    
    @Override
    public void initialize(final EncryptionInfo info, final LittleEndianInput dis) throws IOException {
        this.info = info;
        final int hSize = dis.readInt();
        this.header = new CryptoAPIEncryptionHeader(dis);
        this.verifier = new CryptoAPIEncryptionVerifier(dis, this.header);
        this.decryptor = new CryptoAPIDecryptor(this);
        this.encryptor = new CryptoAPIEncryptor(this);
    }
    
    @Override
    public void initialize(final EncryptionInfo info, CipherAlgorithm cipherAlgorithm, HashAlgorithm hashAlgorithm, int keyBits, final int blockSize, final ChainingMode chainingMode) {
        this.info = info;
        if (cipherAlgorithm == null) {
            cipherAlgorithm = CipherAlgorithm.rc4;
        }
        if (hashAlgorithm == null) {
            hashAlgorithm = HashAlgorithm.sha1;
        }
        if (keyBits == -1) {
            keyBits = 40;
        }
        assert cipherAlgorithm == CipherAlgorithm.rc4 && hashAlgorithm == HashAlgorithm.sha1;
        this.header = new CryptoAPIEncryptionHeader(cipherAlgorithm, hashAlgorithm, keyBits, blockSize, chainingMode);
        this.verifier = new CryptoAPIEncryptionVerifier(cipherAlgorithm, hashAlgorithm, keyBits, blockSize, chainingMode);
        this.decryptor = new CryptoAPIDecryptor(this);
        this.encryptor = new CryptoAPIEncryptor(this);
    }
    
    @Override
    public CryptoAPIEncryptionHeader getHeader() {
        return this.header;
    }
    
    @Override
    public CryptoAPIEncryptionVerifier getVerifier() {
        return this.verifier;
    }
    
    @Override
    public CryptoAPIDecryptor getDecryptor() {
        return this.decryptor;
    }
    
    @Override
    public CryptoAPIEncryptor getEncryptor() {
        return this.encryptor;
    }
    
    public EncryptionInfo getEncryptionInfo() {
        return this.info;
    }
}

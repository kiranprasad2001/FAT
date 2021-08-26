// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.poifs.crypt.binaryrc4;

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

public class BinaryRC4EncryptionInfoBuilder implements EncryptionInfoBuilder
{
    EncryptionInfo info;
    BinaryRC4EncryptionHeader header;
    BinaryRC4EncryptionVerifier verifier;
    BinaryRC4Decryptor decryptor;
    BinaryRC4Encryptor encryptor;
    
    @Override
    public void initialize(final EncryptionInfo info, final LittleEndianInput dis) throws IOException {
        this.info = info;
        final int vMajor = info.getVersionMajor();
        final int vMinor = info.getVersionMinor();
        assert vMajor == 1 && vMinor == 1;
        this.header = new BinaryRC4EncryptionHeader();
        this.verifier = new BinaryRC4EncryptionVerifier(dis);
        this.decryptor = new BinaryRC4Decryptor(this);
        this.encryptor = new BinaryRC4Encryptor(this);
    }
    
    @Override
    public void initialize(final EncryptionInfo info, final CipherAlgorithm cipherAlgorithm, final HashAlgorithm hashAlgorithm, final int keyBits, final int blockSize, final ChainingMode chainingMode) {
        this.info = info;
        this.header = new BinaryRC4EncryptionHeader();
        this.verifier = new BinaryRC4EncryptionVerifier();
        this.decryptor = new BinaryRC4Decryptor(this);
        this.encryptor = new BinaryRC4Encryptor(this);
    }
    
    @Override
    public BinaryRC4EncryptionHeader getHeader() {
        return this.header;
    }
    
    @Override
    public BinaryRC4EncryptionVerifier getVerifier() {
        return this.verifier;
    }
    
    @Override
    public BinaryRC4Decryptor getDecryptor() {
        return this.decryptor;
    }
    
    @Override
    public BinaryRC4Encryptor getEncryptor() {
        return this.encryptor;
    }
    
    public EncryptionInfo getEncryptionInfo() {
        return this.info;
    }
}

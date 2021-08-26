// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.poifs.crypt.cryptoapi;

import org.apache.poi.poifs.crypt.CipherProvider;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.poifs.crypt.ChainingMode;
import org.apache.poi.poifs.crypt.HashAlgorithm;
import org.apache.poi.poifs.crypt.CipherAlgorithm;
import java.io.IOException;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.poifs.crypt.standard.StandardEncryptionHeader;

public class CryptoAPIEncryptionHeader extends StandardEncryptionHeader
{
    public CryptoAPIEncryptionHeader(final LittleEndianInput is) throws IOException {
        super(is);
    }
    
    protected CryptoAPIEncryptionHeader(final CipherAlgorithm cipherAlgorithm, final HashAlgorithm hashAlgorithm, final int keyBits, final int blockSize, final ChainingMode chainingMode) {
        super(cipherAlgorithm, hashAlgorithm, keyBits, blockSize, chainingMode);
    }
    
    public void setKeySize(final int keyBits) {
        boolean found = false;
        for (final int size : this.getCipherAlgorithm().allowedKeySize) {
            if (size == keyBits) {
                found = true;
                break;
            }
        }
        if (!found) {
            throw new EncryptedDocumentException("invalid keysize " + keyBits + " for cipher algorithm " + this.getCipherAlgorithm());
        }
        super.setKeySize(keyBits);
        if (keyBits > 40) {
            this.setCspName("Microsoft Enhanced Cryptographic Provider v1.0");
        }
        else {
            this.setCspName(CipherProvider.rc4.cipherProviderName);
        }
    }
}

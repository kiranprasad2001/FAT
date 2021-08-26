// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.poifs.crypt;

import org.apache.poi.EncryptedDocumentException;

public enum CipherAlgorithm
{
    rc4(CipherProvider.rc4, "RC4", 26625, 64, new int[] { 40, 48, 56, 64, 72, 80, 88, 96, 104, 112, 120, 128 }, -1, 20, "RC4", false), 
    aes128(CipherProvider.aes, "AES", 26126, 128, new int[] { 128 }, 16, 32, "AES", false), 
    aes192(CipherProvider.aes, "AES", 26127, 192, new int[] { 192 }, 16, 32, "AES", false), 
    aes256(CipherProvider.aes, "AES", 26128, 256, new int[] { 256 }, 16, 32, "AES", false), 
    rc2((CipherProvider)null, "RC2", -1, 128, new int[] { 40, 48, 56, 64, 72, 80, 88, 96, 104, 112, 120, 128 }, 8, 20, "RC2", false), 
    des((CipherProvider)null, "DES", -1, 64, new int[] { 64 }, 8, 32, "DES", false), 
    des3((CipherProvider)null, "DESede", -1, 192, new int[] { 192 }, 8, 32, "3DES", false), 
    des3_112((CipherProvider)null, "DESede", -1, 128, new int[] { 128 }, 8, 32, "3DES_112", true), 
    rsa((CipherProvider)null, "RSA", -1, 1024, new int[] { 1024, 2048, 3072, 4096 }, -1, -1, "", false);
    
    public final CipherProvider provider;
    public final String jceId;
    public final int ecmaId;
    public final int defaultKeySize;
    public final int[] allowedKeySize;
    public final int blockSize;
    public final int encryptedVerifierHashLength;
    public final String xmlId;
    public final boolean needsBouncyCastle;
    
    private CipherAlgorithm(final CipherProvider provider, final String jceId, final int ecmaId, final int defaultKeySize, final int[] allowedKeySize, final int blockSize, final int encryptedVerifierHashLength, final String xmlId, final boolean needsBouncyCastle) {
        this.provider = provider;
        this.jceId = jceId;
        this.ecmaId = ecmaId;
        this.defaultKeySize = defaultKeySize;
        this.allowedKeySize = allowedKeySize;
        this.blockSize = blockSize;
        this.encryptedVerifierHashLength = encryptedVerifierHashLength;
        this.xmlId = xmlId;
        this.needsBouncyCastle = needsBouncyCastle;
    }
    
    public static CipherAlgorithm fromEcmaId(final int ecmaId) {
        for (final CipherAlgorithm ca : values()) {
            if (ca.ecmaId == ecmaId) {
                return ca;
            }
        }
        throw new EncryptedDocumentException("cipher algorithm " + ecmaId + " not found");
    }
    
    public static CipherAlgorithm fromXmlId(final String xmlId, final int keySize) {
        for (final CipherAlgorithm ca : values()) {
            if (ca.xmlId.equals(xmlId)) {
                for (final int ks : ca.allowedKeySize) {
                    if (ks == keySize) {
                        return ca;
                    }
                }
            }
        }
        throw new EncryptedDocumentException("cipher algorithm " + xmlId + "/" + keySize + " not found");
    }
}

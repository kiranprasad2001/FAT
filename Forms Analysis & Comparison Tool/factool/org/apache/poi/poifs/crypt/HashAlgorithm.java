// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.poifs.crypt;

import org.apache.poi.EncryptedDocumentException;

public enum HashAlgorithm
{
    none("", 0, "", 0, "", false), 
    sha1("SHA-1", 32772, "SHA1", 20, "HmacSHA1", false), 
    sha256("SHA-256", 32780, "SHA256", 32, "HmacSHA256", false), 
    sha384("SHA-384", 32781, "SHA384", 48, "HmacSHA384", false), 
    sha512("SHA-512", 32782, "SHA512", 64, "HmacSHA512", false), 
    md5("MD5", -1, "MD5", 16, "HmacMD5", false), 
    md2("MD2", -1, "MD2", 16, "Hmac-MD2", true), 
    md4("MD4", -1, "MD4", 16, "Hmac-MD4", true), 
    ripemd128("RipeMD128", -1, "RIPEMD-128", 16, "HMac-RipeMD128", true), 
    ripemd160("RipeMD160", -1, "RIPEMD-160", 20, "HMac-RipeMD160", true), 
    whirlpool("Whirlpool", -1, "WHIRLPOOL", 64, "HMac-Whirlpool", true), 
    sha224("SHA-224", -1, "SHA224", 28, "HmacSHA224", true);
    
    public final String jceId;
    public final int ecmaId;
    public final String ecmaString;
    public final int hashSize;
    public final String jceHmacId;
    public final boolean needsBouncyCastle;
    
    private HashAlgorithm(final String jceId, final int ecmaId, final String ecmaString, final int hashSize, final String jceHmacId, final boolean needsBouncyCastle) {
        this.jceId = jceId;
        this.ecmaId = ecmaId;
        this.ecmaString = ecmaString;
        this.hashSize = hashSize;
        this.jceHmacId = jceHmacId;
        this.needsBouncyCastle = needsBouncyCastle;
    }
    
    public static HashAlgorithm fromEcmaId(final int ecmaId) {
        for (final HashAlgorithm ha : values()) {
            if (ha.ecmaId == ecmaId) {
                return ha;
            }
        }
        throw new EncryptedDocumentException("hash algorithm not found");
    }
    
    public static HashAlgorithm fromEcmaId(final String ecmaString) {
        for (final HashAlgorithm ha : values()) {
            if (ha.ecmaString.equals(ecmaString)) {
                return ha;
            }
        }
        throw new EncryptedDocumentException("hash algorithm not found");
    }
    
    public static HashAlgorithm fromString(final String string) {
        for (final HashAlgorithm ha : values()) {
            if (ha.ecmaString.equalsIgnoreCase(string) || ha.jceId.equalsIgnoreCase(string)) {
                return ha;
            }
        }
        throw new EncryptedDocumentException("hash algorithm not found");
    }
}

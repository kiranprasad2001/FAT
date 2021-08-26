// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.poifs.crypt;

public abstract class EncryptionHeader
{
    public static final int ALGORITHM_RC4;
    public static final int ALGORITHM_AES_128;
    public static final int ALGORITHM_AES_192;
    public static final int ALGORITHM_AES_256;
    public static final int HASH_NONE;
    public static final int HASH_SHA1;
    public static final int HASH_SHA256;
    public static final int HASH_SHA384;
    public static final int HASH_SHA512;
    public static final int PROVIDER_RC4;
    public static final int PROVIDER_AES;
    public static final int MODE_ECB;
    public static final int MODE_CBC;
    public static final int MODE_CFB;
    private int flags;
    private int sizeExtra;
    private CipherAlgorithm cipherAlgorithm;
    private HashAlgorithm hashAlgorithm;
    private int keyBits;
    private int blockSize;
    private CipherProvider providerType;
    private ChainingMode chainingMode;
    private byte[] keySalt;
    private String cspName;
    
    protected EncryptionHeader() {
    }
    
    @Deprecated
    public int getCipherMode() {
        return this.chainingMode.ecmaId;
    }
    
    public ChainingMode getChainingMode() {
        return this.chainingMode;
    }
    
    protected void setChainingMode(final ChainingMode chainingMode) {
        this.chainingMode = chainingMode;
    }
    
    public int getFlags() {
        return this.flags;
    }
    
    protected void setFlags(final int flags) {
        this.flags = flags;
    }
    
    public int getSizeExtra() {
        return this.sizeExtra;
    }
    
    protected void setSizeExtra(final int sizeExtra) {
        this.sizeExtra = sizeExtra;
    }
    
    @Deprecated
    public int getAlgorithm() {
        return this.cipherAlgorithm.ecmaId;
    }
    
    public CipherAlgorithm getCipherAlgorithm() {
        return this.cipherAlgorithm;
    }
    
    protected void setCipherAlgorithm(final CipherAlgorithm cipherAlgorithm) {
        this.cipherAlgorithm = cipherAlgorithm;
    }
    
    @Deprecated
    public int getHashAlgorithm() {
        return this.hashAlgorithm.ecmaId;
    }
    
    public HashAlgorithm getHashAlgorithmEx() {
        return this.hashAlgorithm;
    }
    
    protected void setHashAlgorithm(final HashAlgorithm hashAlgorithm) {
        this.hashAlgorithm = hashAlgorithm;
    }
    
    public int getKeySize() {
        return this.keyBits;
    }
    
    protected void setKeySize(final int keyBits) {
        this.keyBits = keyBits;
    }
    
    public int getBlockSize() {
        return this.blockSize;
    }
    
    protected void setBlockSize(final int blockSize) {
        this.blockSize = blockSize;
    }
    
    public byte[] getKeySalt() {
        return this.keySalt;
    }
    
    protected void setKeySalt(final byte[] salt) {
        this.keySalt = salt;
    }
    
    @Deprecated
    public int getProviderType() {
        return this.providerType.ecmaId;
    }
    
    public CipherProvider getCipherProvider() {
        return this.providerType;
    }
    
    protected void setCipherProvider(final CipherProvider providerType) {
        this.providerType = providerType;
    }
    
    public String getCspName() {
        return this.cspName;
    }
    
    protected void setCspName(final String cspName) {
        this.cspName = cspName;
    }
    
    static {
        ALGORITHM_RC4 = CipherAlgorithm.rc4.ecmaId;
        ALGORITHM_AES_128 = CipherAlgorithm.aes128.ecmaId;
        ALGORITHM_AES_192 = CipherAlgorithm.aes192.ecmaId;
        ALGORITHM_AES_256 = CipherAlgorithm.aes256.ecmaId;
        HASH_NONE = HashAlgorithm.none.ecmaId;
        HASH_SHA1 = HashAlgorithm.sha1.ecmaId;
        HASH_SHA256 = HashAlgorithm.sha256.ecmaId;
        HASH_SHA384 = HashAlgorithm.sha384.ecmaId;
        HASH_SHA512 = HashAlgorithm.sha512.ecmaId;
        PROVIDER_RC4 = CipherProvider.rc4.ecmaId;
        PROVIDER_AES = CipherProvider.aes.ecmaId;
        MODE_ECB = ChainingMode.ecb.ecmaId;
        MODE_CBC = ChainingMode.cbc.ecmaId;
        MODE_CFB = ChainingMode.cfb.ecmaId;
    }
}

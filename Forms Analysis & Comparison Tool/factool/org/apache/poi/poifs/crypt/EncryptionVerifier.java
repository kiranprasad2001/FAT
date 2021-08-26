// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.poifs.crypt;

public abstract class EncryptionVerifier
{
    private byte[] salt;
    private byte[] encryptedVerifier;
    private byte[] encryptedVerifierHash;
    private byte[] encryptedKey;
    private int spinCount;
    private CipherAlgorithm cipherAlgorithm;
    private ChainingMode chainingMode;
    private HashAlgorithm hashAlgorithm;
    
    protected EncryptionVerifier() {
    }
    
    public byte[] getSalt() {
        return this.salt;
    }
    
    @Deprecated
    public byte[] getVerifier() {
        return this.encryptedVerifier;
    }
    
    public byte[] getEncryptedVerifier() {
        return this.encryptedVerifier;
    }
    
    @Deprecated
    public byte[] getVerifierHash() {
        return this.encryptedVerifierHash;
    }
    
    public byte[] getEncryptedVerifierHash() {
        return this.encryptedVerifierHash;
    }
    
    public int getSpinCount() {
        return this.spinCount;
    }
    
    public int getCipherMode() {
        return this.chainingMode.ecmaId;
    }
    
    public int getAlgorithm() {
        return this.cipherAlgorithm.ecmaId;
    }
    
    @Deprecated
    public String getAlgorithmName() {
        return this.cipherAlgorithm.jceId;
    }
    
    public byte[] getEncryptedKey() {
        return this.encryptedKey;
    }
    
    public CipherAlgorithm getCipherAlgorithm() {
        return this.cipherAlgorithm;
    }
    
    public HashAlgorithm getHashAlgorithm() {
        return this.hashAlgorithm;
    }
    
    public ChainingMode getChainingMode() {
        return this.chainingMode;
    }
    
    protected void setSalt(final byte[] salt) {
        this.salt = salt;
    }
    
    protected void setEncryptedVerifier(final byte[] encryptedVerifier) {
        this.encryptedVerifier = encryptedVerifier;
    }
    
    protected void setEncryptedVerifierHash(final byte[] encryptedVerifierHash) {
        this.encryptedVerifierHash = encryptedVerifierHash;
    }
    
    protected void setEncryptedKey(final byte[] encryptedKey) {
        this.encryptedKey = encryptedKey;
    }
    
    protected void setSpinCount(final int spinCount) {
        this.spinCount = spinCount;
    }
    
    protected void setCipherAlgorithm(final CipherAlgorithm cipherAlgorithm) {
        this.cipherAlgorithm = cipherAlgorithm;
    }
    
    protected void setChainingMode(final ChainingMode chainingMode) {
        this.chainingMode = chainingMode;
    }
    
    protected void setHashAlgorithm(final HashAlgorithm hashAlgorithm) {
        this.hashAlgorithm = hashAlgorithm;
    }
}

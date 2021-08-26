// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.poifs.crypt;

import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.poifs.filesystem.NPOIFSFileSystem;
import org.apache.poi.EncryptedDocumentException;
import java.security.GeneralSecurityException;
import java.io.IOException;
import java.io.InputStream;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import javax.crypto.SecretKey;

public abstract class Decryptor
{
    public static final String DEFAULT_PASSWORD = "VelvetSweatshop";
    public static final String DEFAULT_POIFS_ENTRY = "EncryptedPackage";
    protected final EncryptionInfoBuilder builder;
    private SecretKey secretKey;
    private byte[] verifier;
    private byte[] integrityHmacKey;
    private byte[] integrityHmacValue;
    
    protected Decryptor(final EncryptionInfoBuilder builder) {
        this.builder = builder;
    }
    
    public abstract InputStream getDataStream(final DirectoryNode p0) throws IOException, GeneralSecurityException;
    
    public abstract boolean verifyPassword(final String p0) throws GeneralSecurityException;
    
    public abstract long getLength();
    
    public static Decryptor getInstance(final EncryptionInfo info) {
        final Decryptor d = info.getDecryptor();
        if (d == null) {
            throw new EncryptedDocumentException("Unsupported version");
        }
        return d;
    }
    
    public InputStream getDataStream(final NPOIFSFileSystem fs) throws IOException, GeneralSecurityException {
        return this.getDataStream(fs.getRoot());
    }
    
    public InputStream getDataStream(final POIFSFileSystem fs) throws IOException, GeneralSecurityException {
        return this.getDataStream(fs.getRoot());
    }
    
    public byte[] getVerifier() {
        return this.verifier;
    }
    
    public SecretKey getSecretKey() {
        return this.secretKey;
    }
    
    public byte[] getIntegrityHmacKey() {
        return this.integrityHmacKey;
    }
    
    public byte[] getIntegrityHmacValue() {
        return this.integrityHmacValue;
    }
    
    protected void setSecretKey(final SecretKey secretKey) {
        this.secretKey = secretKey;
    }
    
    protected void setVerifier(final byte[] verifier) {
        this.verifier = verifier;
    }
    
    protected void setIntegrityHmacKey(final byte[] integrityHmacKey) {
        this.integrityHmacKey = integrityHmacKey;
    }
    
    protected void setIntegrityHmacValue(final byte[] integrityHmacValue) {
        this.integrityHmacValue = integrityHmacValue;
    }
    
    protected int getBlockSizeInBytes() {
        return this.builder.getHeader().getBlockSize();
    }
    
    protected int getKeySizeInBytes() {
        return this.builder.getHeader().getKeySize() / 8;
    }
}

// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.poifs.crypt;

import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.poifs.filesystem.NPOIFSFileSystem;
import java.security.GeneralSecurityException;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import javax.crypto.SecretKey;

public abstract class Encryptor
{
    protected static final String DEFAULT_POIFS_ENTRY = "EncryptedPackage";
    private SecretKey secretKey;
    
    public abstract OutputStream getDataStream(final DirectoryNode p0) throws IOException, GeneralSecurityException;
    
    public abstract void confirmPassword(final String p0, final byte[] p1, final byte[] p2, final byte[] p3, final byte[] p4, final byte[] p5);
    
    public abstract void confirmPassword(final String p0);
    
    public static Encryptor getInstance(final EncryptionInfo info) {
        return info.getEncryptor();
    }
    
    public OutputStream getDataStream(final NPOIFSFileSystem fs) throws IOException, GeneralSecurityException {
        return this.getDataStream(fs.getRoot());
    }
    
    public OutputStream getDataStream(final POIFSFileSystem fs) throws IOException, GeneralSecurityException {
        return this.getDataStream(fs.getRoot());
    }
    
    public SecretKey getSecretKey() {
        return this.secretKey;
    }
    
    protected void setSecretKey(final SecretKey secretKey) {
        this.secretKey = secretKey;
    }
}

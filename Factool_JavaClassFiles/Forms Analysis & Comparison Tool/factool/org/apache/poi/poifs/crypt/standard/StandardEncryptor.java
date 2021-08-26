// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.poifs.crypt.standard;

import java.io.InputStream;
import org.apache.poi.util.IOUtils;
import java.io.FileInputStream;
import org.apache.poi.util.LittleEndianOutputStream;
import org.apache.poi.poifs.filesystem.POIFSWriterEvent;
import javax.crypto.CipherOutputStream;
import java.io.FileOutputStream;
import org.apache.poi.util.TempFile;
import java.io.File;
import org.apache.poi.poifs.filesystem.POIFSWriterListener;
import java.io.FilterOutputStream;
import org.apache.poi.util.LittleEndianByteArrayOutputStream;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import java.io.IOException;
import org.apache.poi.poifs.filesystem.DirectoryEntry;
import org.apache.poi.poifs.crypt.DataSpaceMapUtils;
import java.io.OutputStream;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import java.security.Key;
import java.security.MessageDigest;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.security.GeneralSecurityException;
import org.apache.poi.EncryptedDocumentException;
import java.util.Arrays;
import org.apache.poi.poifs.crypt.CryptoFunctions;
import org.apache.poi.poifs.crypt.EncryptionVerifier;
import java.util.Random;
import java.security.SecureRandom;
import org.apache.poi.poifs.crypt.Encryptor;

public class StandardEncryptor extends Encryptor
{
    private final StandardEncryptionInfoBuilder builder;
    
    protected StandardEncryptor(final StandardEncryptionInfoBuilder builder) {
        this.builder = builder;
    }
    
    @Override
    public void confirmPassword(final String password) {
        final Random r = new SecureRandom();
        final byte[] salt = new byte[16];
        final byte[] verifier = new byte[16];
        r.nextBytes(salt);
        r.nextBytes(verifier);
        this.confirmPassword(password, null, null, salt, verifier, null);
    }
    
    @Override
    public void confirmPassword(final String password, final byte[] keySpec, final byte[] keySalt, final byte[] verifier, final byte[] verifierSalt, final byte[] integritySalt) {
        final StandardEncryptionVerifier ver = this.builder.getVerifier();
        ver.setSalt(verifierSalt);
        final SecretKey secretKey = StandardDecryptor.generateSecretKey(password, ver, this.getKeySizeInBytes());
        this.setSecretKey(secretKey);
        final Cipher cipher = this.getCipher(secretKey, null);
        try {
            final byte[] encryptedVerifier = cipher.doFinal(verifier);
            final MessageDigest hashAlgo = CryptoFunctions.getMessageDigest(ver.getHashAlgorithm());
            final byte[] calcVerifierHash = hashAlgo.digest(verifier);
            final int encVerHashSize = ver.getCipherAlgorithm().encryptedVerifierHashLength;
            final byte[] encryptedVerifierHash = cipher.doFinal(Arrays.copyOf(calcVerifierHash, encVerHashSize));
            ver.setEncryptedVerifier(encryptedVerifier);
            ver.setEncryptedVerifierHash(encryptedVerifierHash);
        }
        catch (GeneralSecurityException e) {
            throw new EncryptedDocumentException("Password confirmation failed", e);
        }
    }
    
    private Cipher getCipher(final SecretKey key, final String padding) {
        final EncryptionVerifier ver = this.builder.getVerifier();
        return CryptoFunctions.getCipher(key, ver.getCipherAlgorithm(), ver.getChainingMode(), null, 1, padding);
    }
    
    @Override
    public OutputStream getDataStream(final DirectoryNode dir) throws IOException, GeneralSecurityException {
        this.createEncryptionInfoEntry(dir);
        DataSpaceMapUtils.addDefaultDataSpace(dir);
        final OutputStream countStream = new StandardCipherOutputStream(dir);
        return countStream;
    }
    
    protected int getKeySizeInBytes() {
        return this.builder.getHeader().getKeySize() / 8;
    }
    
    protected void createEncryptionInfoEntry(final DirectoryNode dir) throws IOException {
        final EncryptionInfo info = this.builder.getEncryptionInfo();
        final StandardEncryptionHeader header = this.builder.getHeader();
        final StandardEncryptionVerifier verifier = this.builder.getVerifier();
        final EncryptionRecord er = new EncryptionRecord() {
            @Override
            public void write(final LittleEndianByteArrayOutputStream bos) {
                bos.writeShort(info.getVersionMajor());
                bos.writeShort(info.getVersionMinor());
                bos.writeInt(info.getEncryptionFlags());
                header.write(bos);
                verifier.write(bos);
            }
        };
        DataSpaceMapUtils.createEncryptionEntry(dir, "EncryptionInfo", er);
    }
    
    protected class StandardCipherOutputStream extends FilterOutputStream implements POIFSWriterListener
    {
        protected long countBytes;
        protected final File fileOut;
        protected final DirectoryNode dir;
        
        protected StandardCipherOutputStream(final DirectoryNode dir) throws IOException {
            super(null);
            this.dir = dir;
            this.fileOut = TempFile.createTempFile("encrypted_package", "crypt");
            final FileOutputStream rawStream = new FileOutputStream(this.fileOut);
            final CipherOutputStream cryptStream = new CipherOutputStream(rawStream, StandardEncryptor.this.getCipher(StandardEncryptor.this.getSecretKey(), "PKCS5Padding"));
            this.out = cryptStream;
        }
        
        @Override
        public void write(final byte[] b, final int off, final int len) throws IOException {
            this.out.write(b, off, len);
            this.countBytes += len;
        }
        
        @Override
        public void write(final int b) throws IOException {
            this.out.write(b);
            ++this.countBytes;
        }
        
        @Override
        public void close() throws IOException {
            super.close();
            this.writeToPOIFS();
        }
        
        void writeToPOIFS() throws IOException {
            final int oleStreamSize = (int)(this.fileOut.length() + 8L);
            this.dir.createDocument("EncryptedPackage", oleStreamSize, this);
        }
        
        @Override
        public void processPOIFSWriterEvent(final POIFSWriterEvent event) {
            try {
                final LittleEndianOutputStream leos = new LittleEndianOutputStream(event.getStream());
                leos.writeLong(this.countBytes);
                final FileInputStream fis = new FileInputStream(this.fileOut);
                IOUtils.copy(fis, leos);
                fis.close();
                this.fileOut.delete();
                leos.close();
            }
            catch (IOException e) {
                throw new EncryptedDocumentException(e);
            }
        }
    }
}

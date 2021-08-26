// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.poifs.crypt.binaryrc4;

import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.poifs.crypt.ChunkedCipherInputStream;
import java.io.IOException;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import java.io.InputStream;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.util.StringUtil;
import org.apache.poi.poifs.crypt.EncryptionHeader;
import java.security.Key;
import org.apache.poi.poifs.crypt.ChainingMode;
import javax.crypto.spec.SecretKeySpec;
import org.apache.poi.util.LittleEndian;
import java.security.MessageDigest;
import org.apache.poi.poifs.crypt.HashAlgorithm;
import javax.crypto.SecretKey;
import org.apache.poi.poifs.crypt.EncryptionVerifier;
import java.security.GeneralSecurityException;
import org.apache.poi.EncryptedDocumentException;
import java.util.Arrays;
import org.apache.poi.poifs.crypt.CryptoFunctions;
import javax.crypto.Cipher;
import org.apache.poi.poifs.crypt.EncryptionInfoBuilder;
import org.apache.poi.poifs.crypt.Decryptor;

public class BinaryRC4Decryptor extends Decryptor
{
    private long _length;
    
    protected BinaryRC4Decryptor(final BinaryRC4EncryptionInfoBuilder builder) {
        super(builder);
        this._length = -1L;
    }
    
    @Override
    public boolean verifyPassword(final String password) {
        final EncryptionVerifier ver = this.builder.getVerifier();
        final SecretKey skey = generateSecretKey(password, ver);
        try {
            final Cipher cipher = initCipherForBlock(null, 0, this.builder, skey, 2);
            final byte[] encryptedVerifier = ver.getEncryptedVerifier();
            final byte[] verifier = new byte[encryptedVerifier.length];
            cipher.update(encryptedVerifier, 0, encryptedVerifier.length, verifier);
            this.setVerifier(verifier);
            final byte[] encryptedVerifierHash = ver.getEncryptedVerifierHash();
            final byte[] verifierHash = cipher.doFinal(encryptedVerifierHash);
            final HashAlgorithm hashAlgo = ver.getHashAlgorithm();
            final MessageDigest hashAlg = CryptoFunctions.getMessageDigest(hashAlgo);
            final byte[] calcVerifierHash = hashAlg.digest(verifier);
            if (Arrays.equals(calcVerifierHash, verifierHash)) {
                this.setSecretKey(skey);
                return true;
            }
        }
        catch (GeneralSecurityException e) {
            throw new EncryptedDocumentException(e);
        }
        return false;
    }
    
    protected static Cipher initCipherForBlock(Cipher cipher, final int block, final EncryptionInfoBuilder builder, final SecretKey skey, final int encryptMode) throws GeneralSecurityException {
        final EncryptionVerifier ver = builder.getVerifier();
        final HashAlgorithm hashAlgo = ver.getHashAlgorithm();
        final byte[] blockKey = new byte[4];
        LittleEndian.putUInt(blockKey, 0, block);
        final byte[] encKey = CryptoFunctions.generateKey(skey.getEncoded(), hashAlgo, blockKey, 16);
        final SecretKey key = new SecretKeySpec(encKey, skey.getAlgorithm());
        if (cipher == null) {
            final EncryptionHeader em = builder.getHeader();
            cipher = CryptoFunctions.getCipher(key, em.getCipherAlgorithm(), null, null, encryptMode);
        }
        else {
            cipher.init(encryptMode, key);
        }
        return cipher;
    }
    
    protected static SecretKey generateSecretKey(String password, final EncryptionVerifier ver) {
        if (password.length() > 255) {
            password = password.substring(0, 255);
        }
        final HashAlgorithm hashAlgo = ver.getHashAlgorithm();
        final MessageDigest hashAlg = CryptoFunctions.getMessageDigest(hashAlgo);
        byte[] hash = hashAlg.digest(StringUtil.getToUnicodeLE(password));
        final byte[] salt = ver.getSalt();
        hashAlg.reset();
        for (int i = 0; i < 16; ++i) {
            hashAlg.update(hash, 0, 5);
            hashAlg.update(salt);
        }
        hash = new byte[5];
        System.arraycopy(hashAlg.digest(), 0, hash, 0, 5);
        final SecretKey skey = new SecretKeySpec(hash, ver.getCipherAlgorithm().jceId);
        return skey;
    }
    
    @Override
    public InputStream getDataStream(final DirectoryNode dir) throws IOException, GeneralSecurityException {
        final DocumentInputStream dis = dir.createDocumentInputStream("EncryptedPackage");
        this._length = dis.readLong();
        final BinaryRC4CipherInputStream cipherStream = new BinaryRC4CipherInputStream(dis, this._length);
        return cipherStream;
    }
    
    @Override
    public long getLength() {
        if (this._length == -1L) {
            throw new IllegalStateException("Decryptor.getDataStream() was not called");
        }
        return this._length;
    }
    
    private class BinaryRC4CipherInputStream extends ChunkedCipherInputStream
    {
        @Override
        protected Cipher initCipherForBlock(final Cipher existing, final int block) throws GeneralSecurityException {
            return BinaryRC4Decryptor.initCipherForBlock(existing, block, BinaryRC4Decryptor.this.builder, BinaryRC4Decryptor.this.getSecretKey(), 2);
        }
        
        public BinaryRC4CipherInputStream(final DocumentInputStream stream, final long size) throws GeneralSecurityException {
            super(stream, size, 512);
        }
    }
}

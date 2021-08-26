// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.poifs.crypt.standard;

import java.io.IOException;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import javax.crypto.CipherInputStream;
import org.apache.poi.util.BoundedInputStream;
import java.io.InputStream;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.poifs.crypt.EncryptionHeader;
import org.apache.poi.poifs.crypt.ChainingMode;
import org.apache.poi.poifs.crypt.HashAlgorithm;
import javax.crypto.spec.SecretKeySpec;
import org.apache.poi.util.LittleEndian;
import java.security.MessageDigest;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import org.apache.poi.poifs.crypt.EncryptionVerifier;
import java.security.GeneralSecurityException;
import org.apache.poi.EncryptedDocumentException;
import java.util.Arrays;
import org.apache.poi.poifs.crypt.CryptoFunctions;
import org.apache.poi.poifs.crypt.EncryptionInfoBuilder;
import org.apache.poi.poifs.crypt.Decryptor;

public class StandardDecryptor extends Decryptor
{
    private long _length;
    
    protected StandardDecryptor(final EncryptionInfoBuilder builder) {
        super(builder);
        this._length = -1L;
    }
    
    @Override
    public boolean verifyPassword(final String password) {
        final EncryptionVerifier ver = this.builder.getVerifier();
        final SecretKey skey = generateSecretKey(password, ver, this.getKeySizeInBytes());
        final Cipher cipher = this.getCipher(skey);
        try {
            final byte[] encryptedVerifier = ver.getEncryptedVerifier();
            final byte[] verifier = cipher.doFinal(encryptedVerifier);
            this.setVerifier(verifier);
            final MessageDigest sha1 = CryptoFunctions.getMessageDigest(ver.getHashAlgorithm());
            final byte[] calcVerifierHash = sha1.digest(verifier);
            final byte[] encryptedVerifierHash = ver.getEncryptedVerifierHash();
            final byte[] decryptedVerifierHash = cipher.doFinal(encryptedVerifierHash);
            final byte[] verifierHash = Arrays.copyOf(decryptedVerifierHash, calcVerifierHash.length);
            if (Arrays.equals(calcVerifierHash, verifierHash)) {
                this.setSecretKey(skey);
                return true;
            }
            return false;
        }
        catch (GeneralSecurityException e) {
            throw new EncryptedDocumentException(e);
        }
    }
    
    protected static SecretKey generateSecretKey(final String password, final EncryptionVerifier ver, final int keySize) {
        final HashAlgorithm hashAlgo = ver.getHashAlgorithm();
        final byte[] pwHash = CryptoFunctions.hashPassword(password, hashAlgo, ver.getSalt(), ver.getSpinCount());
        final byte[] blockKey = new byte[4];
        LittleEndian.putInt(blockKey, 0, 0);
        final byte[] finalHash = CryptoFunctions.generateKey(pwHash, hashAlgo, blockKey, hashAlgo.hashSize);
        final byte[] x1 = fillAndXor(finalHash, (byte)54);
        final byte[] x2 = fillAndXor(finalHash, (byte)92);
        final byte[] x3 = new byte[x1.length + x2.length];
        System.arraycopy(x1, 0, x3, 0, x1.length);
        System.arraycopy(x2, 0, x3, x1.length, x2.length);
        final byte[] key = Arrays.copyOf(x3, keySize);
        final SecretKey skey = new SecretKeySpec(key, ver.getCipherAlgorithm().jceId);
        return skey;
    }
    
    protected static byte[] fillAndXor(final byte[] hash, final byte fillByte) {
        final byte[] buff = new byte[64];
        Arrays.fill(buff, fillByte);
        for (int i = 0; i < hash.length; ++i) {
            buff[i] ^= hash[i];
        }
        final MessageDigest sha1 = CryptoFunctions.getMessageDigest(HashAlgorithm.sha1);
        return sha1.digest(buff);
    }
    
    private Cipher getCipher(final SecretKey key) {
        final EncryptionHeader em = this.builder.getHeader();
        final ChainingMode cm = em.getChainingMode();
        assert cm == ChainingMode.ecb;
        return CryptoFunctions.getCipher(key, em.getCipherAlgorithm(), cm, null, 2);
    }
    
    @Override
    public InputStream getDataStream(final DirectoryNode dir) throws IOException {
        final DocumentInputStream dis = dir.createDocumentInputStream("EncryptedPackage");
        this._length = dis.readLong();
        final int blockSize = this.builder.getHeader().getCipherAlgorithm().blockSize;
        final long cipherLen = (this._length / blockSize + 1L) * blockSize;
        final Cipher cipher = this.getCipher(this.getSecretKey());
        final InputStream boundedDis = new BoundedInputStream(dis, cipherLen);
        return new BoundedInputStream(new CipherInputStream(boundedDis, cipher), this._length);
    }
    
    @Override
    public long getLength() {
        if (this._length == -1L) {
            throw new IllegalStateException("Decryptor.getDataStream() was not called");
        }
        return this._length;
    }
}

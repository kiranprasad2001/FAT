// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.poifs.crypt;

import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.poifs.filesystem.NPOIFSFileSystem;
import java.io.IOException;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.util.BitField;

public class EncryptionInfo
{
    private final int versionMajor;
    private final int versionMinor;
    private final int encryptionFlags;
    private final EncryptionHeader header;
    private final EncryptionVerifier verifier;
    private final Decryptor decryptor;
    private final Encryptor encryptor;
    public static BitField flagCryptoAPI;
    public static BitField flagDocProps;
    public static BitField flagExternal;
    public static BitField flagAES;
    
    public EncryptionInfo(final POIFSFileSystem fs) throws IOException {
        this(fs.getRoot());
    }
    
    public EncryptionInfo(final NPOIFSFileSystem fs) throws IOException {
        this(fs.getRoot());
    }
    
    public EncryptionInfo(final DirectoryNode dir) throws IOException {
        this(dir.createDocumentInputStream("EncryptionInfo"), false);
    }
    
    public EncryptionInfo(final LittleEndianInput dis, final boolean isCryptoAPI) throws IOException {
        this.versionMajor = dis.readShort();
        this.versionMinor = dis.readShort();
        EncryptionMode encryptionMode;
        if (!isCryptoAPI && this.versionMajor == EncryptionMode.binaryRC4.versionMajor && this.versionMinor == EncryptionMode.binaryRC4.versionMinor) {
            encryptionMode = EncryptionMode.binaryRC4;
            this.encryptionFlags = -1;
        }
        else if (!isCryptoAPI && this.versionMajor == EncryptionMode.agile.versionMajor && this.versionMinor == EncryptionMode.agile.versionMinor) {
            encryptionMode = EncryptionMode.agile;
            this.encryptionFlags = dis.readInt();
        }
        else if (!isCryptoAPI && 2 <= this.versionMajor && this.versionMajor <= 4 && this.versionMinor == EncryptionMode.standard.versionMinor) {
            encryptionMode = EncryptionMode.standard;
            this.encryptionFlags = dis.readInt();
        }
        else {
            if (!isCryptoAPI || 2 > this.versionMajor || this.versionMajor > 4 || this.versionMinor != EncryptionMode.cryptoAPI.versionMinor) {
                this.encryptionFlags = dis.readInt();
                throw new EncryptedDocumentException("Unknown encryption: version major: " + this.versionMajor + " / version minor: " + this.versionMinor + " / fCrypto: " + EncryptionInfo.flagCryptoAPI.isSet(this.encryptionFlags) + " / fExternal: " + EncryptionInfo.flagExternal.isSet(this.encryptionFlags) + " / fDocProps: " + EncryptionInfo.flagDocProps.isSet(this.encryptionFlags) + " / fAES: " + EncryptionInfo.flagAES.isSet(this.encryptionFlags));
            }
            encryptionMode = EncryptionMode.cryptoAPI;
            this.encryptionFlags = dis.readInt();
        }
        EncryptionInfoBuilder eib;
        try {
            eib = getBuilder(encryptionMode);
        }
        catch (Exception e) {
            throw new IOException(e);
        }
        eib.initialize(this, dis);
        this.header = eib.getHeader();
        this.verifier = eib.getVerifier();
        this.decryptor = eib.getDecryptor();
        this.encryptor = eib.getEncryptor();
    }
    
    @Deprecated
    public EncryptionInfo(final POIFSFileSystem fs, final EncryptionMode encryptionMode) {
        this(encryptionMode);
    }
    
    @Deprecated
    public EncryptionInfo(final NPOIFSFileSystem fs, final EncryptionMode encryptionMode) {
        this(encryptionMode);
    }
    
    @Deprecated
    public EncryptionInfo(final DirectoryNode dir, final EncryptionMode encryptionMode) {
        this(encryptionMode);
    }
    
    @Deprecated
    public EncryptionInfo(final POIFSFileSystem fs, final EncryptionMode encryptionMode, final CipherAlgorithm cipherAlgorithm, final HashAlgorithm hashAlgorithm, final int keyBits, final int blockSize, final ChainingMode chainingMode) {
        this(encryptionMode, cipherAlgorithm, hashAlgorithm, keyBits, blockSize, chainingMode);
    }
    
    @Deprecated
    public EncryptionInfo(final NPOIFSFileSystem fs, final EncryptionMode encryptionMode, final CipherAlgorithm cipherAlgorithm, final HashAlgorithm hashAlgorithm, final int keyBits, final int blockSize, final ChainingMode chainingMode) {
        this(encryptionMode, cipherAlgorithm, hashAlgorithm, keyBits, blockSize, chainingMode);
    }
    
    @Deprecated
    public EncryptionInfo(final DirectoryNode dir, final EncryptionMode encryptionMode, final CipherAlgorithm cipherAlgorithm, final HashAlgorithm hashAlgorithm, final int keyBits, final int blockSize, final ChainingMode chainingMode) {
        this(encryptionMode, cipherAlgorithm, hashAlgorithm, keyBits, blockSize, chainingMode);
    }
    
    public EncryptionInfo(final EncryptionMode encryptionMode) {
        this(encryptionMode, null, null, -1, -1, null);
    }
    
    public EncryptionInfo(final EncryptionMode encryptionMode, final CipherAlgorithm cipherAlgorithm, final HashAlgorithm hashAlgorithm, final int keyBits, final int blockSize, final ChainingMode chainingMode) {
        this.versionMajor = encryptionMode.versionMajor;
        this.versionMinor = encryptionMode.versionMinor;
        this.encryptionFlags = encryptionMode.encryptionFlags;
        EncryptionInfoBuilder eib;
        try {
            eib = getBuilder(encryptionMode);
        }
        catch (Exception e) {
            throw new EncryptedDocumentException(e);
        }
        eib.initialize(this, cipherAlgorithm, hashAlgorithm, keyBits, blockSize, chainingMode);
        this.header = eib.getHeader();
        this.verifier = eib.getVerifier();
        this.decryptor = eib.getDecryptor();
        this.encryptor = eib.getEncryptor();
    }
    
    protected static EncryptionInfoBuilder getBuilder(final EncryptionMode encryptionMode) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        final ClassLoader cl = Thread.currentThread().getContextClassLoader();
        final EncryptionInfoBuilder eib = (EncryptionInfoBuilder)cl.loadClass(encryptionMode.builder).newInstance();
        return eib;
    }
    
    public int getVersionMajor() {
        return this.versionMajor;
    }
    
    public int getVersionMinor() {
        return this.versionMinor;
    }
    
    public int getEncryptionFlags() {
        return this.encryptionFlags;
    }
    
    public EncryptionHeader getHeader() {
        return this.header;
    }
    
    public EncryptionVerifier getVerifier() {
        return this.verifier;
    }
    
    public Decryptor getDecryptor() {
        return this.decryptor;
    }
    
    public Encryptor getEncryptor() {
        return this.encryptor;
    }
    
    static {
        EncryptionInfo.flagCryptoAPI = BitFieldFactory.getInstance(4);
        EncryptionInfo.flagDocProps = BitFieldFactory.getInstance(8);
        EncryptionInfo.flagExternal = BitFieldFactory.getInstance(16);
        EncryptionInfo.flagAES = BitFieldFactory.getInstance(32);
    }
}

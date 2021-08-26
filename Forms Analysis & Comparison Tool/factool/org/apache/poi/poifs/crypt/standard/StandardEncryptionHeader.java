// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.poifs.crypt.standard;

import org.apache.poi.util.LittleEndianOutput;
import org.apache.poi.util.StringUtil;
import org.apache.poi.util.LittleEndianByteArrayOutputStream;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import java.io.IOException;
import org.apache.poi.poifs.crypt.ChainingMode;
import java.io.InputStream;
import org.apache.poi.poifs.crypt.CipherProvider;
import org.apache.poi.poifs.crypt.HashAlgorithm;
import org.apache.poi.poifs.crypt.CipherAlgorithm;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.poifs.crypt.EncryptionHeader;

public class StandardEncryptionHeader extends EncryptionHeader implements EncryptionRecord
{
    protected StandardEncryptionHeader(final LittleEndianInput is) throws IOException {
        this.setFlags(is.readInt());
        this.setSizeExtra(is.readInt());
        this.setCipherAlgorithm(CipherAlgorithm.fromEcmaId(is.readInt()));
        this.setHashAlgorithm(HashAlgorithm.fromEcmaId(is.readInt()));
        int keySize = is.readInt();
        if (keySize == 0) {
            keySize = 40;
        }
        this.setKeySize(keySize);
        this.setBlockSize(this.getKeySize());
        this.setCipherProvider(CipherProvider.fromEcmaId(is.readInt()));
        is.readLong();
        ((InputStream)is).mark(5);
        final int checkForSalt = is.readInt();
        ((InputStream)is).reset();
        if (checkForSalt == 16) {
            this.setCspName("");
        }
        else {
            final StringBuilder builder = new StringBuilder();
            while (true) {
                final char c = (char)is.readShort();
                if (c == '\0') {
                    break;
                }
                builder.append(c);
            }
            this.setCspName(builder.toString());
        }
        this.setChainingMode(ChainingMode.ecb);
        this.setKeySalt(null);
    }
    
    protected StandardEncryptionHeader(final CipherAlgorithm cipherAlgorithm, final HashAlgorithm hashAlgorithm, final int keyBits, final int blockSize, final ChainingMode chainingMode) {
        this.setCipherAlgorithm(cipherAlgorithm);
        this.setHashAlgorithm(hashAlgorithm);
        this.setKeySize(keyBits);
        this.setBlockSize(blockSize);
        this.setCipherProvider(cipherAlgorithm.provider);
        this.setFlags(EncryptionInfo.flagCryptoAPI.setBoolean(0, true) | EncryptionInfo.flagAES.setBoolean(0, cipherAlgorithm.provider == CipherProvider.aes));
    }
    
    @Override
    public void write(final LittleEndianByteArrayOutputStream bos) {
        final int startIdx = bos.getWriteIndex();
        final LittleEndianOutput sizeOutput = bos.createDelayedOutput(4);
        bos.writeInt(this.getFlags());
        bos.writeInt(0);
        bos.writeInt(this.getCipherAlgorithm().ecmaId);
        bos.writeInt(this.getHashAlgorithmEx().ecmaId);
        bos.writeInt(this.getKeySize());
        bos.writeInt(this.getCipherProvider().ecmaId);
        bos.writeInt(0);
        bos.writeInt(0);
        String cspName = this.getCspName();
        if (cspName == null) {
            cspName = this.getCipherProvider().cipherProviderName;
        }
        bos.write(StringUtil.getToUnicodeLE(cspName));
        bos.writeShort(0);
        final int headerSize = bos.getWriteIndex() - startIdx - 4;
        sizeOutput.writeInt(headerSize);
    }
}

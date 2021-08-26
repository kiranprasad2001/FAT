// 
// Decompiled by Procyon v0.5.36
// 

package org.apache.poi.poifs.crypt;

import java.io.IOException;
import org.apache.poi.util.LittleEndianInput;

public interface EncryptionInfoBuilder
{
    void initialize(final EncryptionInfo p0, final LittleEndianInput p1) throws IOException;
    
    void initialize(final EncryptionInfo p0, final CipherAlgorithm p1, final HashAlgorithm p2, final int p3, final int p4, final ChainingMode p5);
    
    EncryptionHeader getHeader();
    
    EncryptionVerifier getVerifier();
    
    Decryptor getDecryptor();
    
    Encryptor getEncryptor();
}

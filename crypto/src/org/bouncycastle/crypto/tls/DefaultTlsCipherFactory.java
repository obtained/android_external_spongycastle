package org.bouncycastle.crypto.tls;

import java.io.IOException;

import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.engines.AESFastEngine;
import org.bouncycastle.crypto.engines.DESedeEngine;
import org.bouncycastle.crypto.modes.CBCBlockCipher;

public class DefaultTlsCipherFactory implements TlsCipherFactory
{
    public TlsCipher createCipher(TlsClientContext context, int encryptionAlgorithm, int digestAlgorithm) throws IOException
    {
        switch (encryptionAlgorithm)
        {
            case EncryptionAlgorithm._3DES_EDE_CBC:
                return createDESedeCipher(context, 24, digestAlgorithm);
            case EncryptionAlgorithm.AES_128_CBC:
                return createAESCipher(context, 16, digestAlgorithm);
            case EncryptionAlgorithm.AES_256_CBC:
                return createAESCipher(context, 32, digestAlgorithm);
            default:
                throw new TlsFatalAlert(AlertDescription.internal_error);
        }
    }

    protected TlsCipher createAESCipher(TlsClientContext context, int cipherKeySize, int digestAlgorithm) throws IOException
    {
        return new TlsBlockCipher(context, createAESBlockCipher(),
            createAESBlockCipher(), createDigest(digestAlgorithm), createDigest(digestAlgorithm), cipherKeySize);
    }

    protected TlsCipher createDESedeCipher(TlsClientContext context, int cipherKeySize, int digestAlgorithm) throws IOException
    {
        return new TlsBlockCipher(context, createDESedeBlockCipher(),
            createDESedeBlockCipher(), createDigest(digestAlgorithm), createDigest(digestAlgorithm), cipherKeySize);
    }

    protected BlockCipher createAESBlockCipher()
    {
        return new CBCBlockCipher(new AESFastEngine());
    }

    protected BlockCipher createDESedeBlockCipher()
    {
        return new CBCBlockCipher(new DESedeEngine());
    }

    protected Digest createDigest(int digestAlgorithm) throws IOException
    {
        switch (digestAlgorithm)
        {
            case DigestAlgorithm.SHA:
                return new SHA1Digest();
            default:
                throw new TlsFatalAlert(AlertDescription.internal_error);
        }
    }
}

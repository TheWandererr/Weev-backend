package com.pivo.weev.backend.domain.service.jwt;

import java.io.IOException;
import java.io.StringReader;
import java.security.KeyPair;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMDecryptorProvider;
import org.bouncycastle.openssl.PEMEncryptedKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JcePEMDecryptorProviderBuilder;
import org.bouncycastle.util.io.pem.PemReader;
import org.springframework.stereotype.Component;

@Component
public class PKCS8KeyReader {

    private static final JcaPEMKeyConverter PEM_KEY_CONVERTER = new JcaPEMKeyConverter();

    public KeyPair readKeyPair(String pem, String passphrase) throws IOException {
        PEMEncryptedKeyPair encryptedKeyPair = readEncryptedPem(pem);
        PEMDecryptorProvider decryptor = new JcePEMDecryptorProviderBuilder()
                .setProvider(new BouncyCastleProvider())
                .build(passphrase.toCharArray());
        return PEM_KEY_CONVERTER.getKeyPair(encryptedKeyPair.decryptKeyPair(decryptor));
    }

    private PEMEncryptedKeyPair readEncryptedPem(String pem) throws IOException {
        try (PEMParser pemParser = new PEMParser(new PemReader(new StringReader(pem)))) {
            Object pemObject = pemParser.readObject();
            return (PEMEncryptedKeyPair) pemObject;
        }
    }
}

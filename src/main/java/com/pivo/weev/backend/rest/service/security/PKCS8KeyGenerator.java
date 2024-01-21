package com.pivo.weev.backend.rest.service.security;

import com.pivo.weev.backend.utils.IOUtils;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import org.apache.commons.ssl.PKCS8Key;
import org.springframework.stereotype.Component;

@Component
public class PKCS8KeyGenerator {

    public KeyPair generateKeyPair(String pem, String passphrase) throws IOException, GeneralSecurityException {
        try (InputStream inputStream = new ByteArrayInputStream(IOUtils.getBytes(pem))) {
            PKCS8Key pkcs8 = new PKCS8Key(inputStream, passphrase.toCharArray());
            return new KeyPair(pkcs8.getPublicKey(), pkcs8.getPrivateKey());
        }
    }
}

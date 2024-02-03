package com.pivo.weev.backend.domain.service.jwt;

import com.pivo.weev.backend.config.security.properties.PKCS8KeyProperties;
import com.pivo.weev.backend.utils.IOUtils;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;
import javax.crypto.Cipher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class RSAKeyService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RSAKeyService.class);

    public static final Encoder BASE64_ENCODER = Base64.getEncoder();
    public static final Decoder BASE64_DECODER = Base64.getDecoder();

    private final RSAPrivateKey rsaPrivateKey;
    private final RSAPublicKey rsaPublicKey;
    private final Cipher cipherInstance;

    public RSAKeyService(PKCS8KeyReader pkcs8KeyReader, PKCS8KeyProperties properties) throws Exception {
        LOGGER.info("Initialization of RSA key pair is started");
        KeyPair keyPair = initKeys(pkcs8KeyReader, properties);
        this.rsaPrivateKey = (RSAPrivateKey) keyPair.getPrivate();
        this.rsaPublicKey = (RSAPublicKey) keyPair.getPublic();
        this.cipherInstance = Cipher.getInstance(rsaPrivateKey.getAlgorithm());
        LOGGER.info("Initialization of RSA key pair is finished");
    }

    private KeyPair initKeys(PKCS8KeyReader pkcs8KeyReader, PKCS8KeyProperties properties) throws Exception {
        return pkcs8KeyReader.readKeyPair(properties.getPrivateKey(), properties.getPassphrase());
    }

    public String decrypt(String base64Value) throws GeneralSecurityException {
        cipherInstance.init(Cipher.DECRYPT_MODE, rsaPrivateKey);
        byte[] plainText = cipherInstance.doFinal(BASE64_DECODER.decode(base64Value));
        return new String(plainText);
    }

    public String encrypt(String value) throws GeneralSecurityException {
        cipherInstance.init(Cipher.ENCRYPT_MODE, rsaPublicKey);
        byte[] cipherText = cipherInstance.doFinal(IOUtils.getBytes(value));
        return BASE64_ENCODER.encodeToString(cipherText);
    }

    public RSAPublicKey getPublicKey() {
        return rsaPublicKey;
    }

    public RSAPrivateKey getPrivateKey() {
        return rsaPrivateKey;
    }
}

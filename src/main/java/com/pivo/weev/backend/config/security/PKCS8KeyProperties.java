package com.pivo.weev.backend.config.security;

import static com.pivo.weev.backend.domain.utils.Constants.EncryptionPatterns.PRIVATE_KEY_MASK_SYMBOLS;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.PKCS8_KEY_MASKED_VALUE_NOT_FOUND_ERROR;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.PKCS8_KEY_PASSPHRASE_NOT_FOUND_ERROR;
import static org.apache.commons.lang3.StringUtils.replaceChars;

import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.NotBlank;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("encryption.pks8")
@Setter
public class PKCS8KeyProperties {

    @NotBlank(message = PKCS8_KEY_MASKED_VALUE_NOT_FOUND_ERROR)
    private String maskedPrivateKey;

    private String privateKey;
    @NotBlank(message = PKCS8_KEY_PASSPHRASE_NOT_FOUND_ERROR)
    private String passphrase;

    @PostConstruct
    public void initPrivateKey() {
        privateKey = replaceChars(maskedPrivateKey, PRIVATE_KEY_MASK_SYMBOLS, "\n");
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public String getPassphrase() {
        return passphrase;
    }
}

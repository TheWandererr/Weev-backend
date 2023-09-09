package com.pivo.weev.backend.rest.model.auth;

import static com.pivo.weev.backend.domain.utils.Constants.EncryptionPatterns.PRIVATE_KEY_MASK_SYMBOLS;
import static org.apache.commons.lang3.StringUtils.replaceChars;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "env.encryption.pks8")
@Setter
public class PKCS8KeyProperties {

  private String maskedPrivateKey;

  private String privateKey;
  private String passphrase;

  @PostConstruct
  public void initPrivateKey() throws IOException {
    privateKey = replaceChars(maskedPrivateKey, PRIVATE_KEY_MASK_SYMBOLS, "\n");
  }

  public String getPrivateKey() {
    return privateKey;
  }

  public String getPassphrase() {
    return passphrase;
  }
}

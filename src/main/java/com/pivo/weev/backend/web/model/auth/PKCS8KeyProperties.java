package com.pivo.weev.backend.web.model.auth;

import static com.pivo.weev.backend.domain.utils.Constants.Errors.PKCS8_KEY_MASKED_VALUE_NOT_FOUND_ERROR;
import static com.pivo.weev.backend.domain.utils.Constants.Errors.PKCS8_KEY_PASSPHRASE_NOT_FOUND_ERROR;

import com.pivo.weev.backend.common.env.EnvironmentReader;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class PKCS8KeyProperties extends EnvironmentReader {

  @Value("${environment.property.pkcs8.key.masked.value}")
  private String maskedPrivateKeyProperty;
  @Value("${environment.property.pkcs8.key.password}")
  private String passphraseProperty;

  private String privateKey;
  private String passphrase;

  public PKCS8KeyProperties(Environment env) {
    super(env);
  }

  @PostConstruct
  public void initProperties() throws IOException {
    passphrase = readProperty(passphraseProperty, PKCS8_KEY_PASSPHRASE_NOT_FOUND_ERROR);
    privateKey = readProperty(maskedPrivateKeyProperty, PRIVATE_KEY_MASK_SYMBOLS, "\n", PKCS8_KEY_MASKED_VALUE_NOT_FOUND_ERROR);
  }

  public String getPrivateKey() {
    return privateKey;
  }

  public String getPassphrase() {
    return passphrase;
  }
}

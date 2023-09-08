package com.pivo.weev.backend.domain.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class Constants {

  @UtilityClass
  public static final class Errors {

    public static final String PKCS8_KEY_PASSPHRASE_NOT_FOUND_ERROR = "pkcs8.key.password.not.found";
    public static final String PKCS8_KEY_MASKED_VALUE_NOT_FOUND_ERROR = "pkcs8.key.masked.value.not.found";
    public static final String AUTHENTICATION_PRINCIPAL_NOT_FOUND_ERROR = "authentication.principal.not.found";
    public static final String AUTHORIZATION_TOKEN_NOT_FOUND_ERROR = "authorization.token.not.found";
  }

  @UtilityClass
  public static final class EncryptionPatterns {

    public static final String PRIVATE_KEY_MASK_SYMBOLS = "###";
  }
}

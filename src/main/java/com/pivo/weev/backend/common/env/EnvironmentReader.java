package com.pivo.weev.backend.common.env;

import static java.util.Optional.ofNullable;

import java.io.IOException;
import org.springframework.core.env.Environment;

public abstract class EnvironmentReader {

  public static final String PRIVATE_KEY_MASK_SYMBOLS = "###";

  private final Environment env;

  protected EnvironmentReader(Environment env) {
    this.env = env;
  }

  public String readProperty(String property, String onError) throws IOException {
    return ofNullable(env.getProperty(property)).orElseThrow(() -> new IOException(onError));
  }

  public String readProperty(String property, String mask, String replaceWith, String onError) throws IOException {
    return ofNullable(env.getProperty(property))
        .map(value -> value.replaceAll(mask, replaceWith))
        .orElseThrow(() -> new IOException(onError));
  }
}

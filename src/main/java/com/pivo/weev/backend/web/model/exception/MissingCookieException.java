package com.pivo.weev.backend.web.model.exception;

public class MissingCookieException extends RuntimeException {

  public MissingCookieException(String message) {
    super(message);
  }
}

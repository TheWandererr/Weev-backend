package com.pivo.weev.backend.rest.model.exception;

public class MissingCookieException extends RuntimeException {

  public MissingCookieException(String message) {
    super(message);
  }
}

package com.pivo.weev.backend.rest.model.error;

public class Error {

  private final String errorCode;
  private final String message;

  public Error(String errorCode, String message) {
    this.errorCode = errorCode;
    this.message = message;
  }

  public String getErrorCode() {
    return errorCode;
  }

  public String getMessage() {
    return message;
  }
}

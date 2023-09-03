package com.pivo.weev.backend.dao.exception;

public class ResourceNotFoundException extends RuntimeException {

  public ResourceNotFoundException(String errorCode) {
    super(errorCode);
  }
}

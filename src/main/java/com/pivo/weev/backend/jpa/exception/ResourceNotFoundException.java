package com.pivo.weev.backend.jpa.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String errorCode) {
        super(errorCode);
    }
}

package com.pivo.weev.backend.domain.persistance.jpa.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String code) {
        super(code);
    }

    public String getCode() {
        return getMessage();
    }
}

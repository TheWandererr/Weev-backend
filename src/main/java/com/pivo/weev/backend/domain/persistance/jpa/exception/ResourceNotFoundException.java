package com.pivo.weev.backend.domain.persistance.jpa.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String messageCode) {
        super(messageCode);
    }

    public String getMessageCode() {
        return getMessage();
    }
}

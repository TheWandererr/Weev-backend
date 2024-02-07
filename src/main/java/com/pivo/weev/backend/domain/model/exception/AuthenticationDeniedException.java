package com.pivo.weev.backend.domain.model.exception;

import org.springframework.security.core.AuthenticationException;

public class AuthenticationDeniedException extends AuthenticationException {

    public AuthenticationDeniedException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthenticationDeniedException(String msg) {
        super(msg);
    }
}

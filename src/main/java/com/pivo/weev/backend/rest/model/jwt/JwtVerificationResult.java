package com.pivo.weev.backend.rest.model.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class JwtVerificationResult {

    private final boolean successful;
    private final String failure;
}

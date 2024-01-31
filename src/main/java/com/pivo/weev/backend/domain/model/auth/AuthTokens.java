package com.pivo.weev.backend.domain.model.auth;

import org.springframework.security.oauth2.jwt.Jwt;

public record AuthTokens(Jwt accessToken, Jwt refreshToken) {

    public String getAccessTokenValue() {
        return accessToken.getTokenValue();
    }

    public String getRefreshTokenValue() {
        return refreshToken.getTokenValue();
    }
}

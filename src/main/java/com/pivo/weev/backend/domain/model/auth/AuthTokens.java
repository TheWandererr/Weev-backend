package com.pivo.weev.backend.domain.model.auth;

import static java.util.Objects.nonNull;

import org.springframework.security.oauth2.jwt.Jwt;

public record AuthTokens(Jwt accessToken, Jwt refreshToken) {

    public boolean hasAccessToken() {
        return nonNull(accessToken);
    }

    public boolean hasRefreshToken() {
        return nonNull(refreshToken);
    }


    public String getAccessTokenValue() {
        return hasAccessToken()
                ? accessToken.getTokenValue()
                : null;
    }

    public String getRefreshTokenValue() {
        return hasRefreshToken()
                ? refreshToken.getTokenValue()
                : null;
    }
}

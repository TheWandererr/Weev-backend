package com.pivo.weev.backend.domain.model.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.oauth2.jwt.Jwt;

@Getter
@Setter
@AllArgsConstructor
public class AuthTokens {

    private final Jwt accessToken;
    private final Jwt refreshToken;

    public String getAccessTokenValue() {
        return accessToken.getTokenValue();
    }

    public String getRefreshTokenValue() {
        return refreshToken.getTokenValue();
    }
}

package com.pivo.weev.backend.domain.service.auth;

import static com.pivo.weev.backend.utils.Constants.ErrorCodes.AUTHORIZATION_TOKEN_NOT_FOUND_ERROR;

import com.pivo.weev.backend.domain.model.auth.AuthTokens;
import com.pivo.weev.backend.domain.model.auth.LoginDetails;
import com.pivo.weev.backend.domain.service.jwt.JWTProvider;
import com.pivo.weev.backend.domain.service.jwt.JwtHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthTokensService {

    private final JWTProvider jwtProvider;
    private final LoginDetailsService loginDetailsService;
    private final AuthTokensDetailsService authTokensDetailsService;
    private final JwtHolder jwtHolder;

    public AuthTokens generateTokens(LoginDetails loginDetails) {
        Jwt accessToken = jwtProvider.provideAccessToken(loginDetails);
        Jwt refreshToken = jwtProvider.provideRefreshToken(loginDetails);
        return new AuthTokens(accessToken, refreshToken);
    }

    public AuthTokens refreshTokens() {
        Jwt jwt = jwtHolder.getToken();
        String username = jwt.getSubject();
        LoginDetails loginDetails = (LoginDetails) loginDetailsService.loadUserByUsername(username);
        AuthTokens authTokens = generateTokens(loginDetails);
        boolean updated = authTokensDetailsService.updateTokenDetails(loginDetails, authTokens);
        if (!updated) {
            throw new AuthorizationServiceException(AUTHORIZATION_TOKEN_NOT_FOUND_ERROR);
        }
        return authTokens;
    }
}

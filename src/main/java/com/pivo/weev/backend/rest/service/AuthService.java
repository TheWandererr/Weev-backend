package com.pivo.weev.backend.rest.service;

import static com.pivo.weev.backend.domain.utils.AuthUtils.getAuthenticationDetails;
import static com.pivo.weev.backend.domain.utils.AuthUtils.getLoginDetails;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.AUTHORIZATION_TOKEN_NOT_FOUND_ERROR;

import com.pivo.weev.backend.domain.service.auth.OAuthTokenService;
import com.pivo.weev.backend.rest.model.auth.AuthTokens;
import com.pivo.weev.backend.rest.model.auth.LoginDetails;
import com.pivo.weev.backend.rest.service.security.JWTProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JWTProvider jwtProvider;
    private final LoginDetailsService loginDetailsService;
    private final OAuthTokenService oAuthTokenService;
    private final JwtDecoder jwtDecoder;

    public AuthTokens generateTokens(Authentication authentication) {
        LoginDetails loginDetails = getLoginDetails(authentication);
        return generateTokens(loginDetails);
    }

    private AuthTokens generateTokens(LoginDetails loginDetails) {
        Jwt accessToken = jwtProvider.provideAccessToken(loginDetails);
        Jwt refreshToken = jwtProvider.provideRefreshToken(loginDetails);
        return new AuthTokens(accessToken, refreshToken);
    }

    public AuthTokens refreshTokens(String token) {
        Jwt jwt = jwtDecoder.decode(token);
        String username = jwt.getSubject();
        LoginDetails loginDetails = (LoginDetails) loginDetailsService.loadUserByUsername(username);
        AuthTokens authTokens = generateTokens(loginDetails);
        boolean updated = oAuthTokenService.updateTokenDetails(
                loginDetails.getUserId(),
                loginDetails.getDeviceId(),
                loginDetails.getSerial(),
                authTokens.getRefreshToken().getExpiresAt()
        );
        if (!updated) {
            throw new AuthorizationServiceException(AUTHORIZATION_TOKEN_NOT_FOUND_ERROR);
        }
        return authTokens;
    }

    public void logout() {
        Jwt jwt = getAuthenticationDetails();
        oAuthTokenService.removeTokenDetails(jwt);
    }
}

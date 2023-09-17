package com.pivo.weev.backend.rest.service;

import static com.pivo.weev.backend.domain.utils.AuthUtils.getAuthenticationDetails;
import static com.pivo.weev.backend.domain.utils.AuthUtils.getLoginDetails;
import static com.pivo.weev.backend.domain.utils.Constants.ErrorCodes.AUTHORIZATION_TOKEN_NOT_FOUND_ERROR;

import com.pivo.weev.backend.domain.service.auth.OAuthTokenService;
import com.pivo.weev.backend.rest.model.auth.JWTPair;
import com.pivo.weev.backend.rest.model.auth.LoginDetails;
import com.pivo.weev.backend.rest.service.security.JWTProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
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
    private JwtDecoder jwtDecoder;

    @Autowired
    @Lazy
    public void setJwtDecoder(JwtDecoder jwtDecoder) {
        this.jwtDecoder = jwtDecoder;
    }

    public JWTPair generateTokens(Authentication authentication) {
        LoginDetails loginDetails = getLoginDetails(authentication);
        return generateTokens(loginDetails);
    }

    private JWTPair generateTokens(LoginDetails loginDetails) {
        Jwt accessToken = jwtProvider.provideAccessToken(loginDetails);
        Jwt refreshToken = jwtProvider.provideRefreshToken(loginDetails);
        return new JWTPair(accessToken, refreshToken);
    }

    public JWTPair refreshAuthentication(String token) {
        Jwt jwt = jwtDecoder.decode(token);
        String username = jwt.getSubject();
        LoginDetails loginDetails = (LoginDetails) loginDetailsService.loadUserByUsername(username);
        JWTPair jwtPair = generateTokens(loginDetails);
        boolean updated = oAuthTokenService.updateTokenDetails(
                loginDetails.getUserId(),
                loginDetails.getDeviceId(),
                loginDetails.getSerial(),
                jwtPair.getRefreshToken().getExpiresAt()
        );
        if (!updated) {
            throw new AuthorizationServiceException(AUTHORIZATION_TOKEN_NOT_FOUND_ERROR);
        }
        return jwtPair;
    }

    public void logout() {
        Jwt jwt = getAuthenticationDetails();
        oAuthTokenService.removeTokenDetails(jwt);
    }
}

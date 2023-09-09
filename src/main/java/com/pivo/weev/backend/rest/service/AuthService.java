package com.pivo.weev.backend.rest.service;

import static com.pivo.weev.backend.domain.utils.AuthUtils.getAuthenticationDetails;
import static com.pivo.weev.backend.domain.utils.AuthUtils.getLoginDetails;

import com.pivo.weev.backend.domain.service.OAuthTokenManager;
import com.pivo.weev.backend.rest.model.auth.JWTPair;
import com.pivo.weev.backend.rest.model.auth.LoginDetails;
import com.pivo.weev.backend.rest.service.security.JWTProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final JWTProvider jwtProvider;
  private final LoginDetailsService loginDetailsService;
  private final OAuthTokenManager oAuthTokenManager;
  private JwtDecoder jwtDecoder;

  @Autowired
  @Lazy
  public void setJwtDecoder(JwtDecoder jwtDecoder) {
    this.jwtDecoder = jwtDecoder;
  }

  public JWTPair generateToken(Authentication authentication) {
    LoginDetails loginDetails = getLoginDetails(authentication);
    return generateToken(loginDetails);
  }

  private JWTPair generateToken(LoginDetails loginDetails) {
    Jwt accessToken = jwtProvider.provideAccessToken(loginDetails);
    Jwt refreshToken = jwtProvider.provideRefreshToken(loginDetails);
    return new JWTPair(accessToken, refreshToken);
  }

  public JWTPair refreshAuthentication(String token) {
    Jwt jwt = jwtDecoder.decode(token);
    String username = jwt.getSubject();
    LoginDetails loginDetails = (LoginDetails) loginDetailsService.loadUserByUsername(username);
    oAuthTokenManager.updateTokenDetails(loginDetails);
    return generateToken(loginDetails);
  }

  public void logout() {
    Jwt jwt = getAuthenticationDetails();
    oAuthTokenManager.removeTokenDetails(jwt);
  }
}

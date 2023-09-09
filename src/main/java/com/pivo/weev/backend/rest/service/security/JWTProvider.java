package com.pivo.weev.backend.rest.service.security;

import static com.pivo.weev.backend.common.utils.CollectionUtils.collect;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.MINUTES;
import static org.apache.commons.lang3.StringUtils.SPACE;

import com.pivo.weev.backend.rest.model.auth.LoginDetails;
import com.pivo.weev.backend.rest.utils.Constants.Api;
import com.pivo.weev.backend.rest.utils.Constants.Claims;
import com.pivo.weev.backend.rest.utils.Constants.JWTModes;
import java.time.Instant;
import java.time.temporal.TemporalUnit;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JWTProvider {

  private JwtEncoder jwtEncoder;

  @Autowired
  @Lazy
  public void setJwtEncoder(JwtEncoder jwtEncoder) {
    this.jwtEncoder = jwtEncoder;
  }

  public Jwt provideAccessToken(LoginDetails loginDetails) {
    return generateToken(loginDetails, 30, MINUTES, JWTModes.ACCESS);
  }

  public Jwt provideRefreshToken(LoginDetails loginDetails) {
    return generateToken(loginDetails, 7, DAYS, JWTModes.REFRESH);
  }

  private Jwt generateToken(LoginDetails loginDetails, int expiresAtAmount, TemporalUnit expiresAtUnit, String mode) {
    Instant now = Instant.now();
    String scope = JWTModes.ACCESS.equals(mode)
        ? collect(loginDetails.getAuthenticationAuthorities(), SimpleGrantedAuthority::getAuthority, Collectors.joining(SPACE))
        : mode;
    JwtClaimsSet claimsSet = JwtClaimsSet.builder()
                                         .subject(loginDetails.getUsername())
                                         .audience(List.of(Api.PREFIX))
                                         .issuer(loginDetails.getIssuer())
                                         .issuedAt(now)
                                         .expiresAt(now.plus(expiresAtAmount, expiresAtUnit))
                                         .claim(Claims.DEVICE_ID, loginDetails.getDeviceId())
                                         .claim(Claims.USER_ID, loginDetails.getUserId())
                                         .claim(Claims.SCOPE, scope)
                                         .claim(Claims.SERIAL, loginDetails.getSerial())
                                         .build();
    return jwtEncoder.encode(JwtEncoderParameters.from(claimsSet));
  }
}

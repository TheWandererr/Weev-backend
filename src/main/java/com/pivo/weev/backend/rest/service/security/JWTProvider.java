package com.pivo.weev.backend.rest.service.security;

import static com.pivo.weev.backend.utils.CollectionUtils.collect;
import static java.time.temporal.ChronoUnit.HOURS;
import static org.apache.commons.lang3.StringUtils.SPACE;

import com.pivo.weev.backend.domain.service.config.ConfigsService;
import com.pivo.weev.backend.rest.model.auth.LoginDetails;
import com.pivo.weev.backend.rest.utils.Constants.Api;
import com.pivo.weev.backend.rest.utils.Constants.Claims;
import com.pivo.weev.backend.rest.utils.Constants.JWTModes;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JWTProvider {

    private final JwtEncoder jwtEncoder;
    private final ConfigsService configsService;

    public Jwt provideAccessToken(LoginDetails loginDetails) {
        return generateToken(loginDetails, configsService.getAccessTokenExpiresAmount(), JWTModes.ACCESS);
    }

    public Jwt provideRefreshToken(LoginDetails loginDetails) {
        return generateToken(loginDetails, configsService.getRefreshTokenExpiresAmount(), JWTModes.REFRESH);
    }

    private Jwt generateToken(LoginDetails loginDetails, int expiresAtAmount, String mode) {
        Instant now = Instant.now();
        String scope = JWTModes.ACCESS.equals(mode)
                ? collect(loginDetails.getAuthenticationAuthorities(), SimpleGrantedAuthority::getAuthority, Collectors.joining(SPACE))
                : mode;
        JwtClaimsSet claimsSet = JwtClaimsSet.builder()
                                             .subject(loginDetails.getUsername())
                                             .audience(List.of(Api.PREFIX))
                                             .issuer(loginDetails.getIssuer())
                                             .issuedAt(now)
                                             .expiresAt(now.plus(expiresAtAmount, HOURS))
                                             .claim(Claims.DEVICE_ID, loginDetails.getDeviceId())
                                             .claim(Claims.USER_ID, loginDetails.getUserId())
                                             .claim(Claims.SCOPE, scope)
                                             .claim(Claims.SERIAL, loginDetails.getSerial())
                                             .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(claimsSet));
    }
}

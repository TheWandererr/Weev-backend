package com.pivo.weev.backend.domain.service.jwt;

import static com.pivo.weev.backend.utils.AsyncUtils.runAll;
import static com.pivo.weev.backend.utils.CollectionUtils.collect;
import static java.time.temporal.ChronoUnit.HOURS;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang3.StringUtils.SPACE;

import com.pivo.weev.backend.domain.model.auth.AuthTokens;
import com.pivo.weev.backend.domain.model.auth.LoginDetails;
import com.pivo.weev.backend.domain.service.config.ConfigService;
import com.pivo.weev.backend.rest.utils.Constants.Api;
import com.pivo.weev.backend.rest.utils.Constants.Claims;
import com.pivo.weev.backend.rest.utils.Constants.JWTModes;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class JWTGenerator {

    private final JwtEncoder jwtEncoder;
    private final ConfigService configService;

    private final ThreadPoolTaskExecutor jwtExecutor;

    public AuthTokens generateTokens(LoginDetails loginDetails) {
        Instant now = Instant.now();
        AuthTokens authTokens = new AuthTokens();
        List<Runnable> runnable = List.of(() -> setAccessToken(authTokens, now, loginDetails), () -> setRefreshToken(authTokens, now, loginDetails));
        runAll(jwtExecutor, runnable, throwable -> {
            log.error("Failed to generate tokens");
            return null;
        });
        return authTokens;
    }

    private void setAccessToken(AuthTokens authTokens, Instant now, LoginDetails loginDetails) {
        Jwt token = generateToken(now, loginDetails, configService.getAccessTokenExpiresAmount(), JWTModes.ACCESS);
        authTokens.setAccessToken(token);
    }

    private void setRefreshToken(AuthTokens authTokens, Instant now, LoginDetails loginDetails) {
        Jwt token = generateToken(now, loginDetails, configService.getRefreshTokenExpiresAmount(), JWTModes.REFRESH);
        authTokens.setRefreshToken(token);
    }

    protected Jwt generateToken(Instant now, LoginDetails loginDetails, int expiresAtAmount, String mode) {
        String scope = JWTModes.ACCESS.equals(mode)
                ? collect(loginDetails.authenticationAuthorities(), SimpleGrantedAuthority::getAuthority, joining(SPACE))
                : mode;
        JwtClaimsSet claimsSet = JwtClaimsSet.builder()
                                             .subject(loginDetails.getUsername())
                                             .audience(List.of(Api.PREFIX))
                                             .issuer(loginDetails.issuer())
                                             .issuedAt(now)
                                             .expiresAt(now.plus(expiresAtAmount, HOURS))
                                             .claim(Claims.DEVICE_ID, loginDetails.getDeviceId())
                                             .claim(Claims.USER_ID, loginDetails.getUserId())
                                             .claim(Claims.SCOPE, scope)
                                             .claim(Claims.SERIAL, loginDetails.serial())
                                             .claim(Claims.NICKNAME, loginDetails.getNickname())
                                             .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(claimsSet));
    }
}

package com.pivo.weev.backend.rest.service.security;

import static com.pivo.weev.backend.domain.utils.Constants.ErrorCodes.AUTHORIZATION_TOKEN_NOT_FOUND_ERROR;
import static com.pivo.weev.backend.domain.utils.Constants.ErrorCodes.TOKEN_COMPROMISED_ERROR;
import static com.pivo.weev.backend.domain.utils.JwtUtils.getDeviceId;
import static com.pivo.weev.backend.domain.utils.JwtUtils.getSerial;
import static com.pivo.weev.backend.domain.utils.JwtUtils.getUserId;
import static java.util.Objects.isNull;

import com.pivo.weev.backend.domain.persistance.jpa.model.auth.OAuthTokenDetailsJpa;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.OAuthTokenDetailsRepositoryWrapper;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;

@AllArgsConstructor
public class JWTAuthenticityVerifier {

    private final OAuthTokenDetailsRepositoryWrapper oAuthTokenDetailsRepository;
    private final JwtDecoder jwtDecoder;

    public Pair<Boolean, String> verify(String authorization, String deviceId) {
        try {
            Jwt jwt = jwtDecoder.decode(authorization);
            OAuthTokenDetailsJpa tokenDetails = oAuthTokenDetailsRepository.findByUserIdAndDeviceId(getUserId(jwt), getDeviceId(jwt));
            if (isNull(tokenDetails)) {
                return Pair.of(false, AUTHORIZATION_TOKEN_NOT_FOUND_ERROR);
            }
            if (!StringUtils.equals(getSerial(jwt), tokenDetails.getSerial())) {
                return Pair.of(false, TOKEN_COMPROMISED_ERROR);
            }
            if (!StringUtils.equals(deviceId, tokenDetails.getDeviceId())) {
                return Pair.of(false, TOKEN_COMPROMISED_ERROR);
            }
        } catch (Exception exception) {
            return Pair.of(false, exception.getMessage());
        }
        return Pair.of(true, null);
    }
}

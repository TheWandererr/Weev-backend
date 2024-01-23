package com.pivo.weev.backend.rest.service.security;

import static com.pivo.weev.backend.domain.utils.JwtUtils.getDeviceId;
import static com.pivo.weev.backend.domain.utils.JwtUtils.getSerial;
import static com.pivo.weev.backend.domain.utils.JwtUtils.getUserId;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.AUTHORIZATION_TOKEN_NOT_FOUND_ERROR;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.TOKEN_COMPROMISED_ERROR;
import static java.util.Objects.isNull;

import com.pivo.weev.backend.domain.persistance.jpa.model.auth.OAuthTokenDetailsJpa;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.OAuthTokenDetailsRepositoryWrapper;
import com.pivo.weev.backend.rest.model.auth.JwtVerificationResult;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.oauth2.jwt.Jwt;

@AllArgsConstructor
public class JwtAuthenticityVerifier {

    private final OAuthTokenDetailsRepositoryWrapper oAuthTokenDetailsRepository;

    public JwtVerificationResult verify(Jwt jwt, String deviceId) {
        try {
            OAuthTokenDetailsJpa tokenDetails = oAuthTokenDetailsRepository.findByUserIdAndDeviceId(getUserId(jwt), getDeviceId(jwt));
            if (isNull(tokenDetails)) {
                return new JwtVerificationResult(false, AUTHORIZATION_TOKEN_NOT_FOUND_ERROR);
            }
            if (!StringUtils.equals(getSerial(jwt), tokenDetails.getSerial())) {
                return new JwtVerificationResult(false, TOKEN_COMPROMISED_ERROR);
            }
            if (!StringUtils.equals(deviceId, tokenDetails.getDeviceId())) {
                return new JwtVerificationResult(false, TOKEN_COMPROMISED_ERROR);
            }
        } catch (Exception exception) {
            return new JwtVerificationResult(false, exception.getMessage());
        }
        return new JwtVerificationResult(true, null);
    }
}

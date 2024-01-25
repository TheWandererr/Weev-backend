package com.pivo.weev.backend.rest.service.jwt;

import static com.pivo.weev.backend.domain.utils.JwtUtils.getDeviceId;
import static com.pivo.weev.backend.domain.utils.JwtUtils.getSerial;
import static com.pivo.weev.backend.domain.utils.JwtUtils.getUserId;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.AUTHORIZATION_TOKEN_NOT_FOUND_ERROR;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.TOKEN_COMPROMISED_ERROR;
import static java.util.Objects.isNull;

import com.pivo.weev.backend.domain.persistance.jpa.model.auth.AuthTokensDetailsJpa;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.AuthTokensDetailsRepositoryWrapper;
import com.pivo.weev.backend.rest.model.jwt.JwtVerificationResult;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.oauth2.jwt.Jwt;

@AllArgsConstructor
public class JwtAuthenticityVerifier {

    private final AuthTokensDetailsRepositoryWrapper authTokenDetailsRepository;

    public JwtVerificationResult verify(Jwt jwt, String deviceId) {
        try {
            AuthTokensDetailsJpa tokenDetails = authTokenDetailsRepository.findByUserIdAndDeviceId(getUserId(jwt), getDeviceId(jwt));
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

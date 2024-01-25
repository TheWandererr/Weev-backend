package com.pivo.weev.backend.domain.service.auth;

import static com.pivo.weev.backend.domain.utils.AuthUtils.getAuthenticationDetails;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.ACTIVE_VERIFICATION_REQUEST_ERROR;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.NO_VERIFICATION_REQUEST_ERROR;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.VERIFICATION_CODE_ERROR;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.VERIFICATION_REQUEST_IS_EXPIRED_ERROR;
import static com.pivo.weev.backend.utils.Randomizer.sixDigitInt;
import static java.time.temporal.ChronoUnit.HOURS;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;

import com.pivo.weev.backend.domain.model.exception.FlowInterruptedException;
import com.pivo.weev.backend.domain.model.user.UserSnapshot;
import com.pivo.weev.backend.domain.persistance.document.ValidityPeriod;
import com.pivo.weev.backend.domain.persistance.jpa.model.auth.VerificationRequestJpa;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.VerificationRequestRepositoryWrapper;
import com.pivo.weev.backend.domain.service.config.ConfigService;
import com.pivo.weev.backend.domain.service.message.MessagingService;
import java.time.Instant;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthOperationsService {

    private final AuthTokensDetailsService authTokensDetailsService;
    private final MessagingService messagingService;
    private final ConfigService configService;
    private final VerificationRequestRepositoryWrapper verificationRequestRepository;

    public void logout() {
        Jwt jwt = getAuthenticationDetails();
        authTokensDetailsService.revokeTokenDetails(jwt);
    }

    public void requestEmailVerification(String email) {
        String verificationCode = sixDigitInt();
        VerificationRequestJpa verificationRequestJpa = provideVerificationRequestJpa(email, verificationCode);
        messagingService.sendEmailVerificationMessage(email, verificationCode);
        verificationRequestRepository.save(verificationRequestJpa);
    }

    private VerificationRequestJpa provideVerificationRequestJpa(String email, String verificationCode) {
        Optional<VerificationRequestJpa> optionalExistingRequest = verificationRequestRepository.findByEmail(email);
        if (optionalExistingRequest.isEmpty()) {
            return createVerificationRequestJpa(email, verificationCode);
        }
        VerificationRequestJpa existingRequest = optionalExistingRequest.get();
        if (existingRequest.isCompleted() || existingRequest.isRetryable() || existingRequest.isExpired()) {
            extendVerificationRequest(existingRequest, verificationCode);
            return existingRequest;
        }
        throw new FlowInterruptedException(ACTIVE_VERIFICATION_REQUEST_ERROR, null, FORBIDDEN);
    }

    private VerificationRequestJpa createVerificationRequestJpa(String email, String verificationCode) {
        ValidityPeriod validityPeriod = configService.getVerificationRequestValidityPeriod();
        Instant now = Instant.now();
        return new VerificationRequestJpa(verificationCode,
                                          email,
                                          now.plusSeconds(validityPeriod.getRetryAfterSeconds()),
                                          now.plus(validityPeriod.getExpiresAfterHours(), HOURS)
        );
    }

    private void extendVerificationRequest(VerificationRequestJpa verificationRequest, String verificationCode) {
        ValidityPeriod validityPeriod = configService.getVerificationRequestValidityPeriod();
        Instant now = Instant.now();
        verificationRequest.setCode(verificationCode);
        verificationRequest.setExpiresAt(now.plus(validityPeriod.getExpiresAfterHours(), HOURS));
        verificationRequest.setRetryAfter(now.plusSeconds(validityPeriod.getRetryAfterSeconds()));
        verificationRequest.setCompleted(false);
    }

    @Transactional
    public void completeEmailVerification(String code, String email) {
        VerificationRequestJpa verificationRequest = verificationRequestRepository.findByEmail(email)
                                                                                  .orElseThrow(() -> new FlowInterruptedException(NO_VERIFICATION_REQUEST_ERROR, null, BAD_REQUEST));
        if (verificationRequest.isCompleted()) {
            return;
        }
        if (verificationRequest.isExpired()) {
            throw new FlowInterruptedException(VERIFICATION_REQUEST_IS_EXPIRED_ERROR, null, BAD_REQUEST);
        }
        if (!verificationRequest.getCode().equals(code)) {
            throw new FlowInterruptedException(VERIFICATION_CODE_ERROR, null, BAD_REQUEST);
        }
        verificationRequest.setCompleted(true);
    }

    public void register(UserSnapshot userSnapshot) {

    }
}

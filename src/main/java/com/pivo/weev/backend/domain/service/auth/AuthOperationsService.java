package com.pivo.weev.backend.domain.service.auth;

import static com.pivo.weev.backend.domain.model.auth.VerificationScope.REGISTRATION;
import static com.pivo.weev.backend.domain.utils.AuthUtils.getAuthenticationDetails;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.ACTIVE_VERIFICATION_REQUEST_ERROR;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.EMAIL_ALREADY_USED_ERROR;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.NICKNAME_ALREADY_USED;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.NO_VERIFICATION_REQUEST_ERROR;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.PHONE_NUMBER_ALREADY_USED;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.VERIFICATION_CODE_ERROR;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.VERIFICATION_NOT_COMPLETED;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.VERIFICATION_REQUEST_IS_EXPIRED_ERROR;
import static com.pivo.weev.backend.utils.Randomizer.sixDigitInt;
import static java.time.temporal.ChronoUnit.HOURS;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;

import com.pivo.weev.backend.domain.model.auth.VerificationScope;
import com.pivo.weev.backend.domain.model.exception.FlowInterruptedException;
import com.pivo.weev.backend.domain.model.user.Contacts;
import com.pivo.weev.backend.domain.model.user.UserSnapshot;
import com.pivo.weev.backend.domain.persistance.document.ValidityPeriod;
import com.pivo.weev.backend.domain.persistance.jpa.model.auth.VerificationRequestJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserJpa;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.VerificationRequestRepositoryWrapper;
import com.pivo.weev.backend.domain.service.config.ConfigService;
import com.pivo.weev.backend.domain.service.message.MessagingService;
import com.pivo.weev.backend.domain.service.user.UsersService;
import java.time.Instant;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
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
    private final UsersService usersService;

    public void logout() {
        Jwt jwt = getAuthenticationDetails();
        authTokensDetailsService.revokeTokensDetails(jwt);
    }

    public void requestEmailVerification(String email, VerificationScope verificationScope) {
        if (verificationScope == REGISTRATION) {
            verifyContactsAvailability(new Contacts(email, null));
        }
        String verificationCode = sixDigitInt();
        VerificationRequestJpa verificationRequest = provideVerificationRequest(email, verificationCode);
        messagingService.sendEmailVerificationMessage(email, verificationCode);
        verificationRequestRepository.save(verificationRequest);
    }

    public void verifyContactsAvailability(Contacts contacts) {
        usersService.findUser(contacts)
                    .ifPresent(existingUser -> {
                        String code = defineContactsInaccessibilityError(existingUser, contacts);
                        throw new FlowInterruptedException(code);
                    });
    }

    private String defineContactsInaccessibilityError(UserJpa user, Contacts providedContacts) {
        if (StringUtils.equals(user.getEmail(), providedContacts.getEmail())) {
            return EMAIL_ALREADY_USED_ERROR;
        }
        return PHONE_NUMBER_ALREADY_USED;
    }

    private VerificationRequestJpa provideVerificationRequest(String email, String verificationCode) {
        Optional<VerificationRequestJpa> optionalExistingRequest = verificationRequestRepository.findByEmail(email);
        if (optionalExistingRequest.isEmpty()) {
            return createVerificationRequest(email, verificationCode);
        }
        VerificationRequestJpa existingRequest = optionalExistingRequest.get();
        if (existingRequest.isCompleted() || existingRequest.isRetryable() || existingRequest.isExpired()) {
            extendVerificationRequest(existingRequest, verificationCode);
            return existingRequest;
        }
        throw new FlowInterruptedException(ACTIVE_VERIFICATION_REQUEST_ERROR, null, FORBIDDEN);
    }

    private VerificationRequestJpa createVerificationRequest(String email, String verificationCode) {
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
        completeVerification(verificationRequest, code);
    }

    private void completeVerification(VerificationRequestJpa verificationRequest, String code) {
        if (verificationRequest.isExpired()) {
            throw new FlowInterruptedException(VERIFICATION_REQUEST_IS_EXPIRED_ERROR, null, BAD_REQUEST);
        }
        if (!verificationRequest.getCode().equals(code)) {
            throw new FlowInterruptedException(VERIFICATION_CODE_ERROR, null, BAD_REQUEST);
        }
        verificationRequest.setCompleted(true);
    }

    @Transactional
    public void register(UserSnapshot userSnapshot) {
        verifyRegistrationAvailability(userSnapshot);
        VerificationRequestJpa verificationRequest = verificationRequestRepository.findByEmail(userSnapshot.getContacts().getEmail())
                                                                                  .filter(VerificationRequestJpa::isCompleted)
                                                                                  .orElseThrow(() -> new FlowInterruptedException(VERIFICATION_NOT_COMPLETED, null, BAD_REQUEST));
        usersService.createNewUser(userSnapshot);
        verificationRequestRepository.forceDelete(verificationRequest);
    }

    private void verifyRegistrationAvailability(UserSnapshot userSnapshot) {
        usersService.findUser(userSnapshot)
                    .ifPresent(existingUser -> {
                        String code = defineRegistrationInaccessibilityError(existingUser, userSnapshot);
                        throw new FlowInterruptedException(code, null, BAD_REQUEST);
                    });
    }

    private String defineRegistrationInaccessibilityError(UserJpa existingUser, UserSnapshot userSnapshot) {
        if (existingUser.getNickname().equals(userSnapshot.getNickname())) {
            return NICKNAME_ALREADY_USED;
        }
        return defineContactsInaccessibilityError(existingUser, userSnapshot.getContacts());
    }
}

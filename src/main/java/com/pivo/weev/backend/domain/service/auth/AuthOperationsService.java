package com.pivo.weev.backend.domain.service.auth;

import static com.pivo.weev.backend.domain.model.auth.VerificationScope.REGISTRATION;
import static com.pivo.weev.backend.domain.persistance.jpa.specification.builder.UserSpecificationBuilder.UsernameType.NICKNAME;
import static com.pivo.weev.backend.domain.persistance.jpa.specification.builder.UserSpecificationBuilder.buildUserSearchSpecification;
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
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;

import com.pivo.weev.backend.domain.model.auth.VerificationScope;
import com.pivo.weev.backend.domain.model.exception.FlowInterruptedException;
import com.pivo.weev.backend.domain.model.user.Contacts;
import com.pivo.weev.backend.domain.model.user.UserSnapshot;
import com.pivo.weev.backend.domain.persistance.document.ValidityPeriod;
import com.pivo.weev.backend.domain.persistance.jpa.model.auth.VerificationRequestJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserJpa;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.UserRepositoryWrapper;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.VerificationRequestRepositoryWrapper;
import com.pivo.weev.backend.domain.service.config.ConfigService;
import com.pivo.weev.backend.domain.service.message.MessagingService;
import java.time.Instant;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
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
    private final UserRepositoryWrapper userRepository;
    private final UserFactory userFactory;

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
        Specification<UserJpa> specification = buildUserSearchSpecification(null, contacts.getEmail(), contacts.getPhoneNumber());
        userRepository.find(specification)
                      .ifPresent(existingUser -> {
                          String code = defineContactsInaccessibilityError(existingUser, contacts);
                          throw new FlowInterruptedException(code);
                      });
    }

    private String defineContactsInaccessibilityError(UserJpa user, Contacts providedContacts) {
        if (equalsIgnoreCase(user.getEmail(), providedContacts.getEmail())) {
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
        VerificationRequestJpa verificationRequest = verificationRequestRepository.findByEmail(userSnapshot.getContacts().getEmail())
                                                                                  .filter(VerificationRequestJpa::isCompleted)
                                                                                  .orElseThrow(() -> new FlowInterruptedException(VERIFICATION_NOT_COMPLETED, null, BAD_REQUEST));
        verifyNicknameAvailability(userSnapshot.getNickname());
        UserJpa user = userFactory.createUser(userSnapshot);
        userRepository.save(user);
        verificationRequestRepository.forceDelete(verificationRequest);
    }

    private void verifyNicknameAvailability(String nickname) {
        if (!isNicknameAvailable(nickname)) {
            throw new FlowInterruptedException(NICKNAME_ALREADY_USED, null, BAD_REQUEST);
        }
    }

    public boolean isNicknameAvailable(String nickname) {
        Specification<UserJpa> specification = buildUserSearchSpecification(nickname, NICKNAME);
        return userRepository.find(specification).isEmpty();
    }
}

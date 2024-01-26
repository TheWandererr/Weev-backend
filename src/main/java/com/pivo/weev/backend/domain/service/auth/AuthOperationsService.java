package com.pivo.weev.backend.domain.service.auth;

import static com.pivo.weev.backend.domain.model.auth.VerificationScope.FORGOT_PASSWORD;
import static com.pivo.weev.backend.domain.model.auth.VerificationScope.REGISTRATION;
import static com.pivo.weev.backend.domain.utils.AuthUtils.getAuthenticationDetails;
import static com.pivo.weev.backend.rest.utils.Constants.ResponseDetails.TITLE;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.ACTIVE_VERIFICATION_REQUEST_ERROR;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.NO_VERIFICATION_REQUEST_ERROR;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.USER_NOT_FOUND_ERROR;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.VERIFICATION_CODE_ERROR;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.VERIFICATION_NOT_COMPLETED;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.VERIFICATION_REQUEST_IS_EXPIRED_ERROR;
import static com.pivo.weev.backend.utils.Constants.VerificationMethods.EMAIL;
import static com.pivo.weev.backend.utils.Constants.VerificationMethods.PHONE_NUMBER;
import static com.pivo.weev.backend.utils.Randomizer.sixDigitInt;
import static java.time.temporal.ChronoUnit.HOURS;
import static org.mapstruct.factory.Mappers.getMapper;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;

import com.pivo.weev.backend.domain.mapping.domain.ContactsMapper;
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
import com.pivo.weev.backend.domain.service.validation.AuthOperationsValidator;
import java.time.Instant;
import java.util.Optional;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
    private final AuthOperationsValidator authOperationsValidator;

    public void logout() {
        Jwt jwt = getAuthenticationDetails();
        authTokensDetailsService.revokeTokensDetails(jwt);
    }

    public VerificationRequestJpa requestVerification(Contacts contacts, VerificationScope verificationScope) {
        if (verificationScope == REGISTRATION) {
            authOperationsValidator.validateContactsAvailability(contacts);
        }
        String verificationCode = sixDigitInt();
        VerificationRequestJpa verificationRequest = provideVerificationRequest(contacts, verificationCode);
        sendVerificationCode(verificationRequest, verificationCode);
        return verificationRequestRepository.save(verificationRequest);
    }

    private VerificationRequestJpa provideVerificationRequest(Contacts contacts, String verificationCode) {
        Optional<VerificationRequestJpa> optionalExistingRequest = findVerificationRequest(contacts, request -> true);
        if (optionalExistingRequest.isEmpty()) {
            return createVerificationRequest(contacts, verificationCode);
        }
        VerificationRequestJpa existingRequest = optionalExistingRequest.get();
        if (existingRequest.isCompleted() || existingRequest.isRetryable() || existingRequest.isExpired()) {
            extendVerificationRequest(existingRequest, verificationCode);
            return existingRequest;
        }
        throw new FlowInterruptedException(ACTIVE_VERIFICATION_REQUEST_ERROR, null, FORBIDDEN);
    }

    private VerificationRequestJpa createVerificationRequest(Contacts contacts, String verificationCode) {
        ValidityPeriod validityPeriod = configService.getVerificationRequestValidityPeriod();
        Instant now = Instant.now();
        return new VerificationRequestJpa(verificationCode,
                                          contacts,
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

    private void sendVerificationCode(VerificationRequestJpa verificationRequest, String verificationCode) {
        if (verificationRequest.hasEmail()) {
            messagingService.sendEmailVerificationMessage(verificationRequest.getEmail(), verificationCode);
        }
        if (verificationRequest.hasPhoneNumber()) {
            // TODO
        }
    }

    @Transactional
    public void completeVerification(Contacts contacts, String code) {
        VerificationRequestJpa verificationRequest = findVerificationRequest(contacts, request -> true)
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
        authOperationsValidator.validateRegistrationAvailability(userSnapshot);
        VerificationRequestJpa verificationRequest = findVerificationRequest(userSnapshot.getContacts(), VerificationRequestJpa::isCompleted)
                .orElseThrow(() -> new FlowInterruptedException(VERIFICATION_NOT_COMPLETED, null, BAD_REQUEST));
        usersService.createNewUser(userSnapshot);
        verificationRequestRepository.forceDelete(verificationRequest);
    }

    private Optional<VerificationRequestJpa> findVerificationRequest(Contacts contacts, Predicate<VerificationRequestJpa> filter) {
        return verificationRequestRepository.findByContacts(contacts)
                                            .filter(filter);
    }

    @Transactional
    public String requestPasswordReset(String username) {
        UserJpa user = usersService.findUser(username)
                                   .orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND_ERROR + TITLE));
        Contacts contacts = getMapper(ContactsMapper.class).map(user);
        VerificationRequestJpa verificationRequest = requestVerification(contacts, FORGOT_PASSWORD);
        return verificationRequest.hasEmail() ? EMAIL : PHONE_NUMBER;
    }

    @Transactional
    public void setNewPassword(String newPassword, String username) {
        UserJpa user = usersService.findUser(username)
                                   .orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND_ERROR + TITLE));
        Contacts contacts = getMapper(ContactsMapper.class).map(user);
        VerificationRequestJpa verificationRequest = findVerificationRequest(contacts, VerificationRequestJpa::isCompleted)
                .orElseThrow(() -> new FlowInterruptedException(VERIFICATION_NOT_COMPLETED, null, BAD_REQUEST));
        usersService.updatePassword(user, newPassword);
        verificationRequestRepository.forceDelete(verificationRequest);
    }
}

package com.pivo.weev.backend.domain.service.auth;

import static com.pivo.weev.backend.domain.model.auth.VerificationScope.CHANGE_PASSWORD;
import static com.pivo.weev.backend.domain.model.auth.VerificationScope.FORGOT_PASSWORD;
import static com.pivo.weev.backend.domain.model.auth.VerificationScope.REGISTRATION;
import static com.pivo.weev.backend.domain.utils.JwtUtils.getDeviceId;
import static com.pivo.weev.backend.domain.utils.JwtUtils.getUserId;
import static com.pivo.weev.backend.rest.utils.Constants.ResponseDetails.TITLE;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.ACTIVE_VERIFICATION_REQUEST_ERROR;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.USER_NOT_FOUND_ERROR;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.VERIFICATION_CODE_MATCHING_ERROR;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.VERIFICATION_NOT_COMPLETED_ERROR;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.VERIFICATION_REQUEST_IS_EXPIRED_ERROR;
import static com.pivo.weev.backend.utils.Constants.VerificationMethods.EMAIL;
import static com.pivo.weev.backend.utils.Constants.VerificationMethods.PHONE_NUMBER;
import static com.pivo.weev.backend.utils.Randomizer.sixDigitInt;
import static java.time.temporal.ChronoUnit.HOURS;
import static org.mapstruct.factory.Mappers.getMapper;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;

import com.pivo.weev.backend.config.model.ValidityPeriod;
import com.pivo.weev.backend.domain.mapping.domain.ContactsMapper;
import com.pivo.weev.backend.domain.model.auth.VerificationScope;
import com.pivo.weev.backend.domain.model.exception.FlowInterruptedException;
import com.pivo.weev.backend.domain.model.user.Contacts;
import com.pivo.weev.backend.domain.model.user.UserSnapshot;
import com.pivo.weev.backend.domain.persistance.jpa.model.auth.VerificationRequestJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserJpa;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.UsersRepositoryWrapper;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.VerificationRequestRepositoryWrapper;
import com.pivo.weev.backend.domain.service.config.ConfigService;
import com.pivo.weev.backend.domain.service.jwt.JwtHolder;
import com.pivo.weev.backend.domain.service.message.MailMessagingService;
import com.pivo.weev.backend.domain.service.user.UsersService;
import com.pivo.weev.backend.domain.service.validation.AuthOperationsValidator;
import java.time.Instant;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthOperationsService {

    private final JwtHolder jwtHolder;
    private final AuthTokensDetailsService authTokensDetailsService;
    private final MailMessagingService mailMessagingService;
    private final ConfigService configService;
    private final UsersService usersService;
    private final AuthOperationsValidator authOperationsValidator;

    private final UsersRepositoryWrapper usersRepository;
    private final VerificationRequestRepositoryWrapper verificationRequestRepository;

    @Transactional
    public void logout(boolean allDevices) {
        Jwt jwt = jwtHolder.getToken();
        Long userId = getUserId(jwt);
        logout(userId, allDevices);
    }

    private void logout(Long userId, boolean allDevices) {
        if (allDevices) {
            authTokensDetailsService.revokeTokensDetails(userId);
        } else {
            Jwt jwt = jwtHolder.getToken();
            authTokensDetailsService.revokeTokensDetails(userId, getDeviceId(jwt));
        }
    }

    @Transactional
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
        Optional<VerificationRequestJpa> optionalExistingRequest = findVerificationRequest(contacts);
        if (optionalExistingRequest.isEmpty()) {
            return createVerificationRequest(contacts, verificationCode);
        }
        VerificationRequestJpa existingRequest = optionalExistingRequest.get();
        if (existingRequest.isRetryable() || existingRequest.isExpired()) {
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
    }

    private void sendVerificationCode(VerificationRequestJpa verificationRequest, String verificationCode) {
        if (verificationRequest.hasEmail()) {
            mailMessagingService.sendVerificationMessage(verificationRequest.getEmail(), verificationCode);
        }
        if (verificationRequest.hasPhoneNumber()) {
            // TODO
        }
    }

    private void completeVerification(UserJpa user, String code) {
        Contacts contacts = getMapper(ContactsMapper.class).map(user);
        completeVerification(contacts, code);
    }

    private void completeVerification(Contacts contacts, String code) {
        VerificationRequestJpa verificationRequest = findVerificationRequest(contacts)
                .orElseThrow(() -> new FlowInterruptedException(VERIFICATION_NOT_COMPLETED_ERROR, null, BAD_REQUEST));
        completeVerification(verificationRequest, code);
    }

    private void completeVerification(VerificationRequestJpa verificationRequest, String code) {
        if (verificationRequest.isExpired()) {
            throw new FlowInterruptedException(VERIFICATION_REQUEST_IS_EXPIRED_ERROR, null, BAD_REQUEST);
        }
        if (!verificationRequest.getCode().equals(code)) {
            throw new FlowInterruptedException(VERIFICATION_CODE_MATCHING_ERROR, null, BAD_REQUEST);
        }
        verificationRequestRepository.forceDelete(verificationRequest);
    }

    @Transactional
    public void register(UserSnapshot userSnapshot, String verificationCode) {
        authOperationsValidator.validateRegistrationAvailability(userSnapshot);
        completeVerification(userSnapshot.getContacts(), verificationCode);
        usersService.createUser(userSnapshot);
    }

    private Optional<VerificationRequestJpa> findVerificationRequest(Contacts contacts) {
        return verificationRequestRepository.findByContacts(contacts);
    }

    @Transactional
    public String requestPasswordResetVerification(String username) {
        UserJpa user = usersService.findUser(username)
                                   .orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND_ERROR + TITLE));
        Contacts contacts = getMapper(ContactsMapper.class).map(user);
        VerificationRequestJpa verificationRequest = requestVerification(contacts, FORGOT_PASSWORD);
        return verificationRequest.hasEmail() ? EMAIL : PHONE_NUMBER;
    }

    @Transactional
    public void setNewPassword(String newPassword, String username, String verificationCode) {
        UserJpa user = usersService.findUser(username)
                                   .orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND_ERROR + TITLE));
        completeVerification(user, verificationCode);
        usersService.updatePassword(user, newPassword);
        logout(user.getId(), true);
    }

    @Transactional
    public String requestChangePasswordVerification(Contacts contacts) {
        VerificationRequestJpa verificationRequest = requestVerification(contacts, CHANGE_PASSWORD);
        return verificationRequest.hasEmail() ? EMAIL : PHONE_NUMBER;
    }

    @Transactional
    public void changePassword(String oldPassword, String newPassword, String verificationCode) {
        Jwt jwt = jwtHolder.getToken();
        UserJpa user = usersRepository.fetch(getUserId(jwt));
        completeVerification(user, verificationCode);
        usersService.updatePassword(user, oldPassword, newPassword);
        logout(user.getId(), true);
    }
}

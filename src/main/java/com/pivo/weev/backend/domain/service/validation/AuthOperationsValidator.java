package com.pivo.weev.backend.domain.service.validation;

import static com.pivo.weev.backend.utils.Constants.ErrorCodes.EMAIL_ALREADY_USED_ERROR;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.USED_NICKNAME_ERROR;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.USED_PHONE_NUMBER_ERROR;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import com.pivo.weev.backend.domain.model.exception.FlowInterruptedException;
import com.pivo.weev.backend.domain.model.user.Contacts;
import com.pivo.weev.backend.domain.model.user.RegisteredUserSnapshot;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserJpa;
import com.pivo.weev.backend.domain.service.user.UserResourceService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthOperationsValidator {

    private final UserResourceService userResourceService;

    public void validateContactsAvailability(Contacts contacts) {
        userResourceService.findJpa(contacts)
                           .ifPresent(existingUser -> {
                               String code = defineContactsInaccessibilityError(existingUser, contacts);
                               throw new FlowInterruptedException(code);
                           });
    }

    private String defineContactsInaccessibilityError(UserJpa user, Contacts providedContacts) {
        if (StringUtils.equals(user.getEmail(), providedContacts.getEmail())) {
            return EMAIL_ALREADY_USED_ERROR;
        }
        return USED_PHONE_NUMBER_ERROR;
    }

    public void validateRegistrationAvailability(RegisteredUserSnapshot registeredUserSnapshot) {
        userResourceService.findJpa(registeredUserSnapshot)
                           .ifPresent(existingUser -> {
                               String code = defineRegistrationInaccessibilityError(existingUser, registeredUserSnapshot);
                               throw new FlowInterruptedException(code, null, BAD_REQUEST);
                           });
    }

    private String defineRegistrationInaccessibilityError(UserJpa existingUser, RegisteredUserSnapshot registeredUserSnapshot) {
        if (existingUser.getNickname().equals(registeredUserSnapshot.getNickname())) {
            return USED_NICKNAME_ERROR;
        }
        return defineContactsInaccessibilityError(existingUser, registeredUserSnapshot.getContacts());
    }
}

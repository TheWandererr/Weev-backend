package com.pivo.weev.backend.domain.service.validation;

import static com.pivo.weev.backend.utils.Constants.ErrorCodes.EMAIL_ALREADY_USED_ERROR;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.NICKNAME_ALREADY_USED;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.PHONE_NUMBER_ALREADY_USED;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import com.pivo.weev.backend.domain.model.exception.FlowInterruptedException;
import com.pivo.weev.backend.domain.model.user.Contacts;
import com.pivo.weev.backend.domain.model.user.UserSnapshot;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserJpa;
import com.pivo.weev.backend.domain.service.user.UsersService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthOperationsValidator {

    private final UsersService usersService;

    public void validateContactsAvailability(Contacts contacts) {
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

    public void validateRegistrationAvailability(UserSnapshot userSnapshot) {
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

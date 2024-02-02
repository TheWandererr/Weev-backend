package com.pivo.weev.backend.domain.service.user;

import static com.pivo.weev.backend.utils.Constants.ErrorCodes.PASSWORDS_EQUALITY_ERROR;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.PASSWORD_MATCHING_ERROR;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import com.pivo.weev.backend.domain.model.exception.FlowInterruptedException;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserJpa;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PasswordService {

    private final PasswordEncoder passwordEncoder;

    public String encodePassword(String plainPassword) {
        return passwordEncoder.encode(plainPassword);
    }

    public void validatePasswords(UserJpa user, String oldPassword, String newPassword) {
        boolean matches = passwordEncoder.matches(oldPassword, user.getPassword());
        if (!matches) {
            throw new FlowInterruptedException(PASSWORD_MATCHING_ERROR, null, BAD_REQUEST);
        }
        if (StringUtils.equals(oldPassword, newPassword)) {
            throw new FlowInterruptedException(PASSWORDS_EQUALITY_ERROR, null, BAD_REQUEST);
        }
    }
}

package com.pivo.weev.backend.domain.service.user;

import static com.pivo.weev.backend.utils.Constants.ErrorCodes.PASSWORDS_EQUALITY_ERROR;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.PASSWORD_MATCHING_ERROR;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import com.pivo.weev.backend.domain.model.exception.FlowInterruptedException;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserJpa;
import com.pivo.weev.backend.domain.service.message.DocumentService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserPasswordService {

    private final PasswordEncoder passwordEncoder;

    private final DocumentService documentService;

    public String encodePassword(String plainPassword) {
        return passwordEncoder.encode(plainPassword);
    }

    public void checkPasswordsMatching(UserJpa user, String oldPassword, String newPassword) {
        boolean matches = passwordEncoder.matches(oldPassword, user.getPassword());
        if (!matches) {
            throw new FlowInterruptedException(PASSWORD_MATCHING_ERROR, null, BAD_REQUEST);
        }
        if (StringUtils.equals(oldPassword, newPassword)) {
            throw new FlowInterruptedException(PASSWORDS_EQUALITY_ERROR, null, BAD_REQUEST);
        }
    }

    public void updatePassword(UserJpa user, String newPassword, boolean sendEmail) {
        String encodedPassword = encodePassword(newPassword);
        user.setPassword(encodedPassword);
        if (sendEmail) {
            documentService.sendChangePasswordMail(user.getEmail(), user.getNickname());
        }
    }

    public void updatePassword(UserJpa user, String oldPassword, String newPassword) {
        checkPasswordsMatching(user, oldPassword, newPassword);
        updatePassword(user, newPassword, user.hasEmail());
    }
}

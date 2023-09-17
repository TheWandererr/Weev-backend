package com.pivo.weev.backend.domain.service.validation;

import static com.pivo.weev.backend.domain.utils.Constants.ErrorCodes.EVENT_MODERATION_IMPOSSIBLE_ERROR;
import static com.pivo.weev.backend.domain.utils.Constants.ValidatableFields.LOCAL_START_DATE_TIME;
import static java.time.Instant.now;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import com.pivo.weev.backend.domain.model.exception.ReasonableException;
import com.pivo.weev.backend.domain.persistance.jpa.model.event.EventJpa;
import java.time.Instant;
import org.springframework.stereotype.Service;

@Service
public class ModerationValidator {

    public void validateConfirmation(EventJpa validatable) {
        validateStartDateTime(validatable);
        validateModerationStatus(validatable);
    }

    private void validateStartDateTime(EventJpa validatable) {
        Instant utcStartDateTime = validatable.getUtcStartDateTime();
        if (now().isAfter(utcStartDateTime)) {
            throw new ReasonableException(EVENT_MODERATION_IMPOSSIBLE_ERROR, LOCAL_START_DATE_TIME, BAD_REQUEST);
        }
    }

    private void validateModerationStatus(EventJpa validatable) {
        if (!validatable.isOnModeration()) {
            throw new ReasonableException(EVENT_MODERATION_IMPOSSIBLE_ERROR, LOCAL_START_DATE_TIME, BAD_REQUEST);
        }
    }
}

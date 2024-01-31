package com.pivo.weev.backend.domain.service.validation;

import static com.pivo.weev.backend.domain.utils.Constants.ValidatableFields.LOCAL_START_DATE_TIME;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.OPERATION_IMPOSSIBLE_ERROR;
import static java.time.Instant.now;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import com.pivo.weev.backend.domain.model.exception.FlowInterruptedException;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetJpa;
import java.time.Instant;
import org.springframework.stereotype.Service;

@Service
public class ModerationValidator {

    public void validateConfirmation(MeetJpa validatable) {
        validateStartDateTime(validatable);
        validateModerationStatus(validatable);
    }

    private void validateStartDateTime(MeetJpa validatable) {
        Instant utcStartDateTime = validatable.getUtcStartDateTime();
        if (now().isAfter(utcStartDateTime)) {
            throw new FlowInterruptedException(OPERATION_IMPOSSIBLE_ERROR, LOCAL_START_DATE_TIME, BAD_REQUEST);
        }
    }

    private void validateModerationStatus(MeetJpa validatable) {
        if (!validatable.isOnModeration()) {
            throw new FlowInterruptedException(OPERATION_IMPOSSIBLE_ERROR, null, BAD_REQUEST);
        }
    }
}

package com.pivo.weev.backend.domain.service.validation;

import static com.pivo.weev.backend.common.utils.DateTimeUtils.toInstant;
import static com.pivo.weev.backend.domain.utils.Constants.ErrorCodes.FIELD_VALIDATION_FAILED_ERROR_PATTERN;
import static com.pivo.weev.backend.domain.utils.Constants.ValidatableFields.LOCAL_END_DATE_TIME;
import static com.pivo.weev.backend.domain.utils.Constants.ValidatableFields.LOCAL_START_DATE_TIME;
import static java.lang.String.format;
import static java.time.Instant.now;
import static java.time.temporal.ChronoUnit.HOURS;

import com.pivo.weev.backend.domain.model.event.CreatableEvent;
import com.pivo.weev.backend.domain.model.exception.ReasonableException;
import java.time.Instant;
import org.springframework.stereotype.Service;

@Service
public class EventsOperationsValidator {

    public void validateCreation(CreatableEvent validatable) {
        validateDateTimes(validatable);
    }

    private void validateDateTimes(CreatableEvent validatable) {
        Instant startInstant = toInstant(validatable.getLocalStartDateTime(), validatable.getStartTimeZoneId());
        if (startInstant.isBefore(now().plus(2, HOURS))) {
            throw new ReasonableException(format(FIELD_VALIDATION_FAILED_ERROR_PATTERN, LOCAL_START_DATE_TIME));
        }
        Instant endInstant = toInstant(validatable.getLocalEndDateTime(), validatable.getEndTimeZoneId());
        if (endInstant.isBefore(startInstant) || endInstant.equals(startInstant)) {
            throw new ReasonableException(format(FIELD_VALIDATION_FAILED_ERROR_PATTERN, LOCAL_END_DATE_TIME));
        }
    }
}

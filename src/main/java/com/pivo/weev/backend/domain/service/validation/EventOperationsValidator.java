package com.pivo.weev.backend.domain.service.validation;

import static com.pivo.weev.backend.common.utils.DateTimeUtils.toInstant;
import static com.pivo.weev.backend.domain.persistance.jpa.model.event.EventStatus.CONFIRMED;
import static com.pivo.weev.backend.domain.persistance.jpa.model.event.EventStatus.HAS_MODERATION_INSTANCE;
import static com.pivo.weev.backend.domain.persistance.jpa.model.event.EventStatus.ON_MODERATION;
import static com.pivo.weev.backend.domain.utils.AuthUtils.getUserId;
import static com.pivo.weev.backend.domain.utils.Constants.ErrorCodes.ACCESS_DENIED_ERROR;
import static com.pivo.weev.backend.domain.utils.Constants.ErrorCodes.FIELD_VALIDATION_FAILED_ERROR_PATTERN;
import static com.pivo.weev.backend.domain.utils.Constants.ValidatableFields.LOCAL_END_DATE_TIME;
import static com.pivo.weev.backend.domain.utils.Constants.ValidatableFields.LOCAL_START_DATE_TIME;
import static com.pivo.weev.backend.domain.utils.Constants.ValidatableFields.MEMBERS_LIMIT;
import static java.lang.String.format;
import static java.time.Instant.now;
import static java.time.temporal.ChronoUnit.HOURS;
import static java.util.Objects.nonNull;

import com.pivo.weev.backend.domain.model.event.CreatableEvent;
import com.pivo.weev.backend.domain.model.exception.ReasonableException;
import com.pivo.weev.backend.domain.persistance.jpa.model.event.EventJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.event.EventStatus;
import java.time.Instant;
import java.util.Objects;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class EventOperationsValidator {

    private static final Set<EventStatus> CANCELLABLE_EVENT_STATUSES = Set.of(ON_MODERATION, HAS_MODERATION_INSTANCE, CONFIRMED);

    /*
     * запрещаем создание меньше чем за 2 часа до  начала
     * дата окончания после начала
     * */
    public void validateCreation(CreatableEvent validatable) {
        Instant startInstant = toInstant(validatable.getLocalStartDateTime(), validatable.getStartTimeZoneId());
        if (startInstant.isBefore(now().plus(2, HOURS))) {
            throw new ReasonableException(format(FIELD_VALIDATION_FAILED_ERROR_PATTERN, LOCAL_START_DATE_TIME));
        }
        Instant endInstant = toInstant(validatable.getLocalEndDateTime(), validatable.getEndTimeZoneId());
        if (endInstant.isBefore(startInstant) || endInstant.equals(startInstant)) {
            throw new ReasonableException(format(FIELD_VALIDATION_FAILED_ERROR_PATTERN, LOCAL_END_DATE_TIME));
        }
    }

    /*
     * запрещаем редактирование за 3 часа до начала
     * количество участников уже больше, чем лимит в запросе
     * проверяем владельца
     * это не скрытый ивент (созданный для обновления другого ивента)
     * */
    public void validateUpdate(EventJpa updatable, CreatableEvent validatable) {
        Instant startInstant = toInstant(validatable.getLocalStartDateTime(), validatable.getStartTimeZoneId());
        if (!now().plus(3, HOURS).isBefore(startInstant)) {
            throw new ReasonableException(format(FIELD_VALIDATION_FAILED_ERROR_PATTERN, LOCAL_START_DATE_TIME));
        }
        int membersLimit = validatable.getMembersLimit();
        if (membersLimit > 0 && updatable.getMembers().size() > membersLimit) {
            throw new ReasonableException(format(FIELD_VALIDATION_FAILED_ERROR_PATTERN, MEMBERS_LIMIT));
        }
        if (!Objects.equals(getUserId(), updatable.getCreator().getId())) {
            throw new ReasonableException(ACCESS_DENIED_ERROR);
        }
        if (nonNull(updatable.getUpdatableTarget())) {
            throw new ReasonableException(ACCESS_DENIED_ERROR);
        }
    }

    /*
     * запрещаем отмену за 3 часа до начала
     * проверяем владельца
     * это не скрытый ивент (созданный для обновления другого ивента)
     * проверяем статусы
     * */
    public void validateCancellation(EventJpa cancellable) {
        Instant startInstant = toInstant(cancellable.getLocalStartDateTime(), cancellable.getStartTimeZoneId());
        if (!now().plus(3, HOURS).isBefore(startInstant)) {
            throw new ReasonableException(format(FIELD_VALIDATION_FAILED_ERROR_PATTERN, LOCAL_START_DATE_TIME));
        }
        if (!Objects.equals(getUserId(), cancellable.getCreator().getId())) {
            throw new ReasonableException(ACCESS_DENIED_ERROR);
        }
        if (nonNull(cancellable.getUpdatableTarget())) {
            throw new ReasonableException(ACCESS_DENIED_ERROR);
        }
        if (!CANCELLABLE_EVENT_STATUSES.contains(cancellable.getStatus())) {
            throw new ReasonableException(ACCESS_DENIED_ERROR);
        }
    }
}

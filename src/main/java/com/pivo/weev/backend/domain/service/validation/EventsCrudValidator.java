package com.pivo.weev.backend.domain.service.validation;

import static com.pivo.weev.backend.domain.model.event.Restrictions.Availability.PUBLIC;
import static com.pivo.weev.backend.domain.model.event.Restrictions.Availability.RESTRICTED;
import static com.pivo.weev.backend.domain.persistance.jpa.model.event.EventStatus.CANCELED;
import static com.pivo.weev.backend.domain.persistance.jpa.model.event.EventStatus.CONFIRMED;
import static com.pivo.weev.backend.domain.persistance.jpa.model.event.EventStatus.DECLINED;
import static com.pivo.weev.backend.domain.persistance.jpa.model.event.EventStatus.HAS_MODERATION_INSTANCE;
import static com.pivo.weev.backend.domain.persistance.jpa.model.event.EventStatus.ON_MODERATION;
import static com.pivo.weev.backend.domain.utils.AuthUtils.getUserId;
import static com.pivo.weev.backend.domain.utils.Constants.ValidatableFields.LOCAL_END_DATE_TIME;
import static com.pivo.weev.backend.domain.utils.Constants.ValidatableFields.LOCAL_START_DATE_TIME;
import static com.pivo.weev.backend.domain.utils.Constants.ValidatableFields.MEMBERS_LIMIT;
import static com.pivo.weev.backend.utils.CollectionUtils.isPresent;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.ACCESS_DENIED_ERROR;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.EVENT_FINISHED_ERROR;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.FIELD_VALIDATION_FAILED_ERROR_PATTERN;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.OPERATION_IMPOSSIBLE_ERROR;
import static com.pivo.weev.backend.utils.Constants.Reasons.EVENT_ALREADY_JOINED;
import static com.pivo.weev.backend.utils.Constants.Reasons.EVENT_CAPACITY_EXCEEDED;
import static com.pivo.weev.backend.utils.DateTimeUtils.toInstant;
import static java.lang.String.format;
import static java.time.Instant.now;
import static java.time.temporal.ChronoUnit.HOURS;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.springframework.http.HttpStatus.FORBIDDEN;

import com.pivo.weev.backend.domain.model.event.CreatableEvent;
import com.pivo.weev.backend.domain.model.exception.FlowInterruptedException;
import com.pivo.weev.backend.domain.persistance.jpa.model.event.EventJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.event.EventStatus;
import com.pivo.weev.backend.domain.persistance.jpa.model.event.RestrictionsJpa;
import java.time.Instant;
import java.util.Objects;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class EventsCrudValidator {

    private static final Set<EventStatus> CANCELLABLE_EVENT_STATUSES = Set.of(ON_MODERATION, HAS_MODERATION_INSTANCE, CONFIRMED);
    private static final Set<EventStatus> UPDATABLE_EVENT_STATUSES = Set.of(ON_MODERATION, HAS_MODERATION_INSTANCE, CONFIRMED, DECLINED, CANCELED);
    private static final Set<EventStatus> DELETABLE_EVENT_STATUSES = Set.of(CANCELED, DECLINED);

    /*
     * запрещаем создание меньше чем за 2 часа до  начала
     * дата окончания после начала
     * */
    public void validateCreation(CreatableEvent validatable) {
        Instant startInstant = toInstant(validatable.getLocalStartDateTime(), validatable.getStartTimeZoneId());
        if (startInstant.isBefore(now().plus(2, HOURS))) {
            throw new FlowInterruptedException(format(FIELD_VALIDATION_FAILED_ERROR_PATTERN, LOCAL_START_DATE_TIME));
        }
        Instant endInstant = toInstant(validatable.getLocalEndDateTime(), validatable.getEndTimeZoneId());
        if (endInstant.isBefore(startInstant) || endInstant.equals(startInstant)) {
            throw new FlowInterruptedException(format(FIELD_VALIDATION_FAILED_ERROR_PATTERN, LOCAL_END_DATE_TIME));
        }
    }

    /*
     * запрещаем редактирование за 3 часа до начала
     * запрещаем, если количество участников уже больше, чем лимит в запросе
     * проверяем владельца
     * это не скрытый ивент (созданный для обновления другого ивента)
     * проверяем статусы
     * */
    public void validateUpdate(EventJpa updatable, CreatableEvent validatable) {
        Instant startInstant = toInstant(validatable.getLocalStartDateTime(), validatable.getStartTimeZoneId());
        if (!now().plus(3, HOURS).isBefore(startInstant)) {
            throw new FlowInterruptedException(format(FIELD_VALIDATION_FAILED_ERROR_PATTERN, LOCAL_START_DATE_TIME));
        }
        int membersLimit = validatable.getMembersLimit();
        if (membersLimit > 0 && updatable.getMembers().size() > membersLimit) {
            throw new FlowInterruptedException(format(FIELD_VALIDATION_FAILED_ERROR_PATTERN, MEMBERS_LIMIT));
        }
        if (!Objects.equals(getUserId(), updatable.getCreator().getId())) {
            throw new FlowInterruptedException(ACCESS_DENIED_ERROR, null, FORBIDDEN);
        }
        if (nonNull(updatable.getUpdatableTarget())) {
            throw new FlowInterruptedException(ACCESS_DENIED_ERROR, null, FORBIDDEN);
        }
        if (!UPDATABLE_EVENT_STATUSES.contains(updatable.getStatus())) {
            throw new FlowInterruptedException(ACCESS_DENIED_ERROR, null, FORBIDDEN);
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
            throw new FlowInterruptedException(format(FIELD_VALIDATION_FAILED_ERROR_PATTERN, LOCAL_START_DATE_TIME));
        }
        if (!Objects.equals(getUserId(), cancellable.getCreator().getId())) {
            throw new FlowInterruptedException(ACCESS_DENIED_ERROR, null, FORBIDDEN);
        }
        if (nonNull(cancellable.getUpdatableTarget())) {
            throw new FlowInterruptedException(ACCESS_DENIED_ERROR, null, FORBIDDEN);
        }
        if (!CANCELLABLE_EVENT_STATUSES.contains(cancellable.getStatus())) {
            throw new FlowInterruptedException(ACCESS_DENIED_ERROR, null, FORBIDDEN);
        }
    }

    public void validateDeletion(EventJpa deletable) {
        if (!DELETABLE_EVENT_STATUSES.contains(deletable.getStatus())) {
            throw new FlowInterruptedException(ACCESS_DENIED_ERROR, null, FORBIDDEN);
        }
    }

    /**
     * для ивента не требуется заявка или приглашение
     * создатель не может присоединиться
     * ивент должен быть общедоступным
     * ивент должен быть активным
     * в ивенте должны быть места
     * юзер не должен быть участником
     */
    public void validateJoin(EventJpa event) {
        validateJoinAvailability(event);
        RestrictionsJpa restrictions = event.getRestrictions();
        if (!PUBLIC.name().equals(restrictions.getAvailability())) {
            throw new FlowInterruptedException(ACCESS_DENIED_ERROR, null, FORBIDDEN);
        }
    }

    private void validateJoinAvailability(EventJpa event) {
        Long userId = getUserId();
        if (Objects.equals(userId, event.getCreator().getId())) {
            throw new FlowInterruptedException(ACCESS_DENIED_ERROR, null, FORBIDDEN);
        }
        if (!event.isPublished()) {
            throw new FlowInterruptedException(ACCESS_DENIED_ERROR, null, FORBIDDEN);
        }
        if (event.isEnded()) {
            throw new FlowInterruptedException(EVENT_FINISHED_ERROR, null, FORBIDDEN);
        }
        RestrictionsJpa restrictions = event.getRestrictions();
        if (event.isStarted() && isTrue(restrictions.getJoinAfterStartDisallowed())) {
            throw new FlowInterruptedException(ACCESS_DENIED_ERROR, null, FORBIDDEN);
        }
        if (event.hasMembersLimit()) {
            int members = event.getMembers().size();
            if (members + 1 > event.getMembersLimit()) {
                throw new FlowInterruptedException(OPERATION_IMPOSSIBLE_ERROR, EVENT_CAPACITY_EXCEEDED);
            }
        }
        if (isPresent(event.getMembers(), member -> Objects.equals(member.getId(), userId))) {
            throw new FlowInterruptedException(OPERATION_IMPOSSIBLE_ERROR, EVENT_ALREADY_JOINED);
        }
    }

    /**
     * для ивента требуется заявка
     * создатель не может присоединиться
     * ивент должен быть общедоступным
     * ивент должен быть активным
     * в ивенте должны быть места
     * юзер не должен быть участником
     */
    public void validateCreateJoinRequest(EventJpa event) {
        RestrictionsJpa restrictions = event.getRestrictions();
        if (!RESTRICTED.name().equals(restrictions.getAvailability())) {
            throw new FlowInterruptedException(ACCESS_DENIED_ERROR, null, FORBIDDEN);
        }
        validateJoinAvailability(event);
    }
}

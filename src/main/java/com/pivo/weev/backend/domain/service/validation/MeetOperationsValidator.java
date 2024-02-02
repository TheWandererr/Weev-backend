package com.pivo.weev.backend.domain.service.validation;

import static com.pivo.weev.backend.domain.model.meet.Restrictions.Availability.RESTRICTED;
import static com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetStatus.CANCELED;
import static com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetStatus.CONFIRMED;
import static com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetStatus.DECLINED;
import static com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetStatus.HAS_MODERATION_INSTANCE;
import static com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetStatus.ON_MODERATION;
import static com.pivo.weev.backend.domain.utils.AuthUtils.getUserId;
import static com.pivo.weev.backend.domain.utils.Constants.ValidatableFields.LOCAL_END_DATE_TIME;
import static com.pivo.weev.backend.domain.utils.Constants.ValidatableFields.LOCAL_START_DATE_TIME;
import static com.pivo.weev.backend.domain.utils.Constants.ValidatableFields.MEMBERS_LIMIT;
import static com.pivo.weev.backend.utils.CollectionUtils.isPresent;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.ACCESS_DENIED_ERROR;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.FIELD_VALIDATION_FAILED_ERROR_PATTERN;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.MEET_IS_FINISHED;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.OPERATION_IMPOSSIBLE_ERROR;
import static com.pivo.weev.backend.utils.Constants.Reasons.MEET_ALREADY_JOINED;
import static com.pivo.weev.backend.utils.Constants.Reasons.MEET_CAPACITY_EXCEEDED;
import static com.pivo.weev.backend.utils.Constants.Reasons.MEET_JOIN_REQUEST_IS_EXPIRED;
import static com.pivo.weev.backend.utils.DateTimeUtils.toInstant;
import static java.lang.String.format;
import static java.time.Instant.now;
import static java.time.temporal.ChronoUnit.HOURS;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.springframework.http.HttpStatus.FORBIDDEN;

import com.pivo.weev.backend.domain.model.exception.FlowInterruptedException;
import com.pivo.weev.backend.domain.model.meet.CreatableMeet;
import com.pivo.weev.backend.domain.model.meet.Restrictions.Availability;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetRequestJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetStatus;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.RestrictionsJpa;
import java.time.Instant;
import java.util.Objects;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class MeetOperationsValidator {

    private static final Set<MeetStatus> CANCELLABLE_MEET_STATUSES = Set.of(ON_MODERATION, HAS_MODERATION_INSTANCE, CONFIRMED);
    private static final Set<MeetStatus> UPDATABLE_MEET_STATUSES = Set.of(ON_MODERATION, HAS_MODERATION_INSTANCE, CONFIRMED, DECLINED, CANCELED);
    private static final Set<MeetStatus> DELETABLE_MEET_STATUSES = Set.of(CANCELED, DECLINED);

    /*
     * запрещаем создание меньше чем за 2 часа до  начала
     * дата окончания после начала
     * */
    public void validateCreation(CreatableMeet validatable) {
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
    public void validateUpdate(MeetJpa updatable, CreatableMeet validatable) {
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
        if (!UPDATABLE_MEET_STATUSES.contains(updatable.getStatus())) {
            throw new FlowInterruptedException(ACCESS_DENIED_ERROR, null, FORBIDDEN);
        }
    }

    /*
     * запрещаем отмену за 3 часа до начала
     * проверяем владельца
     * это не скрытый ивент (созданный для обновления другого ивента)
     * проверяем статусы
     * */
    public void validateCancellation(MeetJpa cancellable) {
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
        if (!CANCELLABLE_MEET_STATUSES.contains(cancellable.getStatus())) {
            throw new FlowInterruptedException(ACCESS_DENIED_ERROR, null, FORBIDDEN);
        }
    }

    public void validateDeletion(MeetJpa deletable) {
        if (!DELETABLE_MEET_STATUSES.contains(deletable.getStatus())) {
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
    public void validateJoin(MeetJpa meet, Long joinerId, Availability expectedAvailability) {
        validateJoinAvailability(meet, joinerId);
        RestrictionsJpa restrictions = meet.getRestrictions();
        if (!expectedAvailability.name().equals(restrictions.getAvailability())) {
            throw new FlowInterruptedException(ACCESS_DENIED_ERROR, null, FORBIDDEN);
        }
    }

    private void validateJoinAvailability(MeetJpa meet, Long joinerId) {
        if (Objects.equals(joinerId, meet.getCreator().getId())) {
            throw new FlowInterruptedException(ACCESS_DENIED_ERROR, null, FORBIDDEN);
        }
        if (!meet.isPublished()) {
            throw new FlowInterruptedException(ACCESS_DENIED_ERROR, null, FORBIDDEN);
        }
        if (meet.isEnded()) {
            throw new FlowInterruptedException(MEET_IS_FINISHED, null, FORBIDDEN);
        }
        RestrictionsJpa restrictions = meet.getRestrictions();
        if (meet.isStarted() && isTrue(restrictions.getJoinAfterStartDisallowed())) {
            throw new FlowInterruptedException(ACCESS_DENIED_ERROR, null, FORBIDDEN);
        }
        if (meet.hasMembersLimit()) {
            int members = meet.getMembers().size();
            if (members + 1 > meet.getMembersLimit()) {
                throw new FlowInterruptedException(OPERATION_IMPOSSIBLE_ERROR, MEET_CAPACITY_EXCEEDED);
            }
        }
        if (isPresent(meet.getMembers(), member -> Objects.equals(member.getId(), joinerId))) {
            throw new FlowInterruptedException(OPERATION_IMPOSSIBLE_ERROR, MEET_ALREADY_JOINED);
        }
    }

    /*
     * создатель не может присоединиться
     * ивент должен быть общедоступным
     * ивент должен быть активным
     * в ивенте должны быть места
     * юзер не должен быть участником
     */
    public void validateJoinRequestCreation(MeetJpa meet, Long joinerId) {
        validateJoin(meet, joinerId, RESTRICTED);
    }

    /**
     * Заявка должна быть актуальной
     * Заявку принимает владелец ивента
     */
    public void validateJoinRequestConfirmation(MeetRequestJpa request, MeetJpa meet) {
        if (request.isExpired()) {
            throw new FlowInterruptedException(OPERATION_IMPOSSIBLE_ERROR, MEET_JOIN_REQUEST_IS_EXPIRED, FORBIDDEN);
        }
        if (!Objects.equals(getUserId(), meet.getCreator().getId())) {
            throw new FlowInterruptedException(ACCESS_DENIED_ERROR, null, FORBIDDEN);
        }
    }

    public void validateJoinRequestDeclination(MeetJpa meet) {
        if (!Objects.equals(getUserId(), meet.getCreator().getId())) {
            throw new FlowInterruptedException(ACCESS_DENIED_ERROR, null, FORBIDDEN);
        }
    }
}

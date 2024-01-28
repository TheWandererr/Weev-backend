package com.pivo.weev.backend.domain.service.event;

import static com.pivo.weev.backend.domain.utils.AuthUtils.getUserId;
import static com.pivo.weev.backend.domain.utils.Constants.NotificationDetails.REQUESTER;
import static com.pivo.weev.backend.domain.utils.Constants.NotificationTitles.EVENT_JOIN_REQUEST_CONFIRMATION;
import static com.pivo.weev.backend.domain.utils.Constants.NotificationTitles.EVENT_NEW_JOIN_REQUEST;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.OPERATION_IMPOSSIBLE_ERROR;
import static com.pivo.weev.backend.utils.Constants.Reasons.EVENT_JOIN_REQUEST_ALREADY_CREATED;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.springframework.http.HttpStatus.FORBIDDEN;

import com.pivo.weev.backend.domain.model.exception.FlowInterruptedException;
import com.pivo.weev.backend.domain.persistance.jpa.model.event.EventJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.event.EventRequestJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.event.RestrictionsJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserJpa;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.EventRequestsRepositoryWrapper;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.EventsRepositoryWrapper;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.UsersRepositoryWrapper;
import com.pivo.weev.backend.domain.service.NotificationService;
import com.pivo.weev.backend.domain.service.validation.EventsOperationsValidator;
import java.time.Instant;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EventRequestsService {

    private final EventsRepositoryWrapper eventsRepository;
    private final EventRequestsRepositoryWrapper eventRequestsRepository;
    private final UsersRepositoryWrapper usersRepository;

    private final EventsOperationsValidator eventsOperationsValidator;
    private final NotificationService notificationService;
    private final EventsOperationsService eventsOperationsService;

    @Transactional
    public void createJoinRequest(Long id) {
        EventJpa event = eventsRepository.fetch(id);
        eventsOperationsValidator.validateJoinRequestCreation(event, getUserId());
        UserJpa user = usersRepository.fetch(getUserId());
        eventRequestsRepository.findByEventIdAndUserId(event.getId(), user.getId())
                               .ifPresent(request -> {
                                   throw new FlowInterruptedException(OPERATION_IMPOSSIBLE_ERROR, EVENT_JOIN_REQUEST_ALREADY_CREATED, FORBIDDEN);
                               });
        notificationService.notify(event, event.getCreator(), EVENT_NEW_JOIN_REQUEST, Map.of(REQUESTER, user.getId()));
        eventRequestsRepository.save(new EventRequestJpa(event, user, getEventRequestExpirationTime(event)));
    }

    private Instant getEventRequestExpirationTime(EventJpa event) {
        RestrictionsJpa restrictions = event.getRestrictions();
        if (isTrue(restrictions.getJoinAfterStartDisallowed())) {
            return event.getUtcStartDateTime();
        }
        return event.getUtcEndDateTime();
    }

    @Transactional
    public void confirmJoinRequest(Long eventId, Long requestId) {
        EventRequestJpa request = eventRequestsRepository.fetch(requestId);
        EventJpa event = eventsRepository.fetch(eventId);
        eventsOperationsValidator.validateJoinRequestConfirmation(request, event);
        UserJpa joiner = request.getUser();
        eventsOperationsService.joinViaRequest(event, joiner);
        notificationService.notify(event, joiner, EVENT_JOIN_REQUEST_CONFIRMATION);
        eventRequestsRepository.forceDelete(request);
    }
}

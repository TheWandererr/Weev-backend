package com.pivo.weev.backend.domain.service.meet;

import static com.pivo.weev.backend.domain.utils.AuthUtils.getUserId;
import static com.pivo.weev.backend.domain.utils.Constants.NotificationDetails.REQUESTER;
import static com.pivo.weev.backend.domain.utils.Constants.NotificationTitles.MEET_JOIN_REQUEST_CONFIRMATION;
import static com.pivo.weev.backend.domain.utils.Constants.NotificationTitles.MEET_NEW_JOIN_REQUEST;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.OPERATION_IMPOSSIBLE_ERROR;
import static com.pivo.weev.backend.utils.Constants.Reasons.MEET_JOIN_REQUEST_ALREADY_CREATED;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.springframework.http.HttpStatus.FORBIDDEN;

import com.pivo.weev.backend.domain.model.exception.FlowInterruptedException;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetRequestJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.RestrictionsJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserJpa;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.MeetRepositoryWrapper;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.MeetRequestsRepositoryWrapper;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.UsersRepositoryWrapper;
import com.pivo.weev.backend.domain.service.NotificationService;
import com.pivo.weev.backend.domain.service.validation.MeetOperationsValidator;
import java.time.Instant;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MeetRequestsService {

    private final MeetRepositoryWrapper meetRepository;
    private final MeetRequestsRepositoryWrapper eventRequestsRepository;
    private final UsersRepositoryWrapper usersRepository;

    private final MeetOperationsValidator meetOperationsValidator;
    private final NotificationService notificationService;
    private final MeetOperationsService meetOperationsService;

    @Transactional
    public void createJoinRequest(Long id) {
        MeetJpa event = meetRepository.fetch(id);
        meetOperationsValidator.validateJoinRequestCreation(event, getUserId());
        UserJpa user = usersRepository.fetch(getUserId());
        eventRequestsRepository.findByMeetIdAndUserId(event.getId(), user.getId())
                               .ifPresent(request -> {
                                   throw new FlowInterruptedException(OPERATION_IMPOSSIBLE_ERROR, MEET_JOIN_REQUEST_ALREADY_CREATED, FORBIDDEN);
                               });
        notificationService.notify(event, event.getCreator(), MEET_NEW_JOIN_REQUEST, Map.of(REQUESTER, user.getId()));
        eventRequestsRepository.save(new MeetRequestJpa(event, user, getEventRequestExpirationTime(event)));
    }

    private Instant getEventRequestExpirationTime(MeetJpa event) {
        RestrictionsJpa restrictions = event.getRestrictions();
        if (isTrue(restrictions.getJoinAfterStartDisallowed())) {
            return event.getUtcStartDateTime();
        }
        return event.getUtcEndDateTime();
    }

    @Transactional
    public void confirmJoinRequest(Long eventId, Long requestId) {
        MeetRequestJpa request = eventRequestsRepository.fetch(requestId);
        MeetJpa event = meetRepository.fetch(eventId);
        meetOperationsValidator.validateJoinRequestConfirmation(request, event);
        UserJpa joiner = request.getUser();
        meetOperationsService.joinViaRequest(event, joiner);
        notificationService.notify(event, joiner, MEET_JOIN_REQUEST_CONFIRMATION);
        eventRequestsRepository.forceDelete(request);
    }
}

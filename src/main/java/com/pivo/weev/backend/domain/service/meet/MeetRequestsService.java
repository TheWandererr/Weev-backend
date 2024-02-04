package com.pivo.weev.backend.domain.service.meet;

import static com.pivo.weev.backend.domain.utils.AuthUtils.getUserId;
import static com.pivo.weev.backend.domain.utils.Constants.NotificationDetails.REQUESTER_ID;
import static com.pivo.weev.backend.domain.utils.Constants.NotificationDetails.REQUESTER_NICKNAME;
import static com.pivo.weev.backend.domain.utils.Constants.NotificationTopics.MEET_JOIN_REQUEST_CONFIRMATION;
import static com.pivo.weev.backend.domain.utils.Constants.NotificationTopics.MEET_JOIN_REQUEST_DECLINATION;
import static com.pivo.weev.backend.domain.utils.Constants.NotificationTopics.MEET_NEW_JOIN_REQUEST;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.OPERATION_IMPOSSIBLE_ERROR;
import static com.pivo.weev.backend.utils.Constants.Reasons.MEET_JOIN_REQUEST_ALREADY_CREATED;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.springframework.http.HttpStatus.FORBIDDEN;

import com.pivo.weev.backend.domain.model.exception.FlowInterruptedException;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetJoinRequestJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.RestrictionsJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserJpa;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.MeetJoinRequestsRepositoryWrapper;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.MeetRepositoryWrapper;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.UsersRepositoryWrapper;
import com.pivo.weev.backend.domain.service.event.ApplicationEventFactory;
import com.pivo.weev.backend.domain.service.event.model.PushNotificationEvent;
import com.pivo.weev.backend.domain.service.message.NotificationService;
import com.pivo.weev.backend.domain.service.validation.MeetOperationsValidator;
import java.time.Instant;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MeetRequestsService {

    private final MeetRepositoryWrapper meetRepository;
    private final MeetJoinRequestsRepositoryWrapper meetJoinRequestsRepository;
    private final UsersRepositoryWrapper usersRepository;

    private final MeetOperationsValidator meetOperationsValidator;
    private final NotificationService notificationService;
    private final MeetOperationsService meetOperationsService;

    private final ApplicationEventPublisher applicationEventPublisher;
    private final ApplicationEventFactory applicationEventFactory;

    @Transactional
    public void createJoinRequest(Long id) {
        MeetJpa meet = meetRepository.fetch(id);
        meetOperationsValidator.validateJoinRequestCreation(meet, getUserId());
        UserJpa user = usersRepository.fetch(getUserId());
        if (meetJoinRequestsRepository.existsByMeetIdAndUserId(meet.getId(), user.getId())) {
            throw new FlowInterruptedException(OPERATION_IMPOSSIBLE_ERROR, MEET_JOIN_REQUEST_ALREADY_CREATED, FORBIDDEN);
        }
        notify(meet, meet.getCreator(), MEET_NEW_JOIN_REQUEST, Map.of(REQUESTER_ID, user.getId(), REQUESTER_NICKNAME, user.getNickname()));
        meetJoinRequestsRepository.save(new MeetJoinRequestJpa(meet, user, getMeetRequestExpirationTime(meet)));
    }

    private void notify(MeetJpa meet, UserJpa user, String topic, Map<String, Object> details) {
        notificationService.notify(meet, user, topic, details);
        PushNotificationEvent event = applicationEventFactory.buildNotificationEvent(meet, user, topic, details);
        applicationEventPublisher.publishEvent(event);
    }

    private Instant getMeetRequestExpirationTime(MeetJpa meet) {
        RestrictionsJpa restrictions = meet.getRestrictions();
        if (isTrue(restrictions.getJoinAfterStartDisallowed())) {
            return meet.getUtcStartDateTime();
        }
        return meet.getUtcEndDateTime();
    }

    @Transactional
    public void confirmJoinRequest(Long requestId) {
        MeetJoinRequestJpa request = meetJoinRequestsRepository.fetch(requestId);
        meetOperationsValidator.validateJoinRequestConfirmation(request);
        MeetJpa meet = request.getMeet();
        UserJpa joiner = request.getUser();
        meetOperationsService.joinViaRequest(meet, joiner);
        notify(meet, joiner, MEET_JOIN_REQUEST_CONFIRMATION, null);
        meetJoinRequestsRepository.forceDelete(request);
    }

    @Transactional
    public void declineJoinRequest(Long requestId) {
        MeetJoinRequestJpa request = meetJoinRequestsRepository.fetch(requestId);
        meetOperationsValidator.validateJoinRequestDeclination(request);
        notify(request.getMeet(), request.getUser(), MEET_JOIN_REQUEST_DECLINATION, null);
        meetJoinRequestsRepository.forceDelete(request);
    }
}

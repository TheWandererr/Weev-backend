package com.pivo.weev.backend.domain.service.meet;

import static com.pivo.weev.backend.domain.persistance.utils.PageableUtils.build;
import static com.pivo.weev.backend.domain.utils.AuthUtils.getUserId;
import static com.pivo.weev.backend.domain.utils.Constants.NotificationDetails.REQUESTER_ID;
import static com.pivo.weev.backend.domain.utils.Constants.NotificationDetails.REQUESTER_NICKNAME;
import static com.pivo.weev.backend.domain.utils.Constants.NotificationTopics.MEET_JOIN_REQUEST_CONFIRMATION;
import static com.pivo.weev.backend.domain.utils.Constants.NotificationTopics.MEET_JOIN_REQUEST_DECLINATION;
import static com.pivo.weev.backend.domain.utils.Constants.NotificationTopics.MEET_NEW_JOIN_REQUEST;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.OPERATION_IMPOSSIBLE_ERROR;
import static com.pivo.weev.backend.utils.Constants.Reasons.MEET_JOIN_REQUEST_ALREADY_CREATED;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.mapstruct.factory.Mappers.getMapper;
import static org.springframework.http.HttpStatus.FORBIDDEN;

import com.pivo.weev.backend.domain.mapping.domain.MeetRequestMapper;
import com.pivo.weev.backend.domain.model.event.PushNotificationEvent;
import com.pivo.weev.backend.domain.model.exception.FlowInterruptedException;
import com.pivo.weev.backend.domain.model.meet.MeetJoinRequest;
import com.pivo.weev.backend.domain.model.meet.SearchParams.PageCriteria;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetJoinRequestJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.RestrictionsJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserJpa;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.MeetJoinRequestsRepository;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.MeetRepository;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.UsersRepository;
import com.pivo.weev.backend.domain.service.event.factory.ApplicationEventFactory;
import com.pivo.weev.backend.domain.service.message.NotificationService;
import com.pivo.weev.backend.domain.service.validation.MeetOperationsValidator;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MeetRequestsService {

    private final MeetRepository meetRepository;
    private final MeetJoinRequestsRepository meetJoinRequestsRepository;
    private final UsersRepository usersRepository;

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
        PushNotificationEvent event = applicationEventFactory.buildPushNotificationEvent(meet, user, topic, details);
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

    @Transactional
    public Page<MeetJoinRequest> getMeetJoinRequests(Long meetId, PageCriteria pageCriteria) {
        Pageable pageable = build(pageCriteria.getPage(), pageCriteria.getPageSize(), new String[0]);
        Page<MeetJoinRequestJpa> jpaPage = meetJoinRequestsRepository.findAllByMeetId(meetId, pageable);
        List<MeetJoinRequest> content = getMapper(MeetRequestMapper.class).map(jpaPage.getContent());
        return new PageImpl<>(content, jpaPage.getPageable(), jpaPage.getTotalElements());
    }

    public void deleteAllRequests(Long userId) {
        meetJoinRequestsRepository.deleteAllByUserId(userId);
    }
}

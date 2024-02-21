package com.pivo.weev.backend.domain.service.meet;

import static com.pivo.weev.backend.domain.persistance.utils.PageableUtils.build;
import static com.pivo.weev.backend.domain.utils.AuthUtils.getUserId;
import static com.pivo.weev.backend.domain.utils.Constants.MessagingPayload.MEET;
import static com.pivo.weev.backend.domain.utils.Constants.MessagingPayload.USER;
import static com.pivo.weev.backend.domain.utils.Constants.NotificationTopics.MEET_JOIN_REQUEST_CONFIRMATION;
import static com.pivo.weev.backend.domain.utils.Constants.NotificationTopics.MEET_JOIN_REQUEST_DECLINATION;
import static com.pivo.weev.backend.domain.utils.Constants.NotificationTopics.MEET_NEW_JOIN_REQUEST;
import static com.pivo.weev.backend.utils.Constants.ErrorCodes.OPERATION_IMPOSSIBLE_ERROR;
import static com.pivo.weev.backend.utils.Constants.Reasons.MEET_JOIN_REQUEST_ALREADY_CREATED;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.mapstruct.factory.Mappers.getMapper;
import static org.springframework.http.HttpStatus.FORBIDDEN;

import com.pivo.weev.backend.domain.mapping.domain.MeetPayloadMapper;
import com.pivo.weev.backend.domain.mapping.domain.MeetRequestMapper;
import com.pivo.weev.backend.domain.mapping.domain.UserPayloadMapper;
import com.pivo.weev.backend.domain.model.event.PushNotificationEvent;
import com.pivo.weev.backend.domain.model.event.payload.MeetPayload;
import com.pivo.weev.backend.domain.model.event.payload.UserPayload;
import com.pivo.weev.backend.domain.model.exception.FlowInterruptedException;
import com.pivo.weev.backend.domain.model.meet.MeetJoinRequest;
import com.pivo.weev.backend.domain.model.meet.SearchParams.PageCriteria;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetJoinRequestJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.RestrictionsJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserJpa;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.MeetJoinRequestsRepository;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.MeetRepository;
import com.pivo.weev.backend.domain.service.event.factory.ApplicationEventFactory;
import com.pivo.weev.backend.domain.service.messaging.NotificationService;
import com.pivo.weev.backend.domain.service.user.UserResourceService;
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

    private final MeetOperationsValidator meetOperationsValidator;
    private final NotificationService notificationService;
    private final MeetOperationsService meetOperationsService;
    private final UserResourceService userResourceService;

    private final ApplicationEventPublisher applicationEventPublisher;
    private final ApplicationEventFactory applicationEventFactory;

    @Transactional
    public void createJoinRequest(Long id) {
        MeetJpa meet = meetRepository.fetch(id);
        Long userId = getUserId();
        meetOperationsValidator.validateJoinRequestCreation(meet, userId);
        UserJpa user = userResourceService.fetchJpa(userId);
        if (meetJoinRequestsRepository.existsByMeetIdAndUserId(meet.getId(), userId)) {
            throw new FlowInterruptedException(OPERATION_IMPOSSIBLE_ERROR, MEET_JOIN_REQUEST_ALREADY_CREATED, FORBIDDEN);
        }

        UserPayload userPayload = getMapper(UserPayloadMapper.class).map(user);
        MeetPayload meetPayload = getMapper(MeetPayloadMapper.class).map(meet);

        notificationService.notify(meet, meet.getCreator(), MEET_NEW_JOIN_REQUEST, Map.of(USER, userPayload));
        publishPushNotificationEvent(user, MEET_NEW_JOIN_REQUEST, Map.of(USER, userPayload, MEET, meetPayload));

        meetJoinRequestsRepository.save(new MeetJoinRequestJpa(meet, user, getMeetRequestExpirationTime(meet)));
    }

    private void publishPushNotificationEvent(UserJpa user, String topic, Map<String, Object> payload) {
        UserPayload userPayload = getMapper(UserPayloadMapper.class).map(user);
        PushNotificationEvent event = applicationEventFactory.buildPushNotificationEvent(userPayload, topic, payload);
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
        meetOperationsValidator.validateJoinRequestConfirmation(request, getUserId());
        MeetJpa meet = request.getMeet();
        UserJpa joiner = request.getUser();
        meetOperationsService.joinViaRequest(meet, joiner);

        notificationService.notify(meet, joiner, MEET_JOIN_REQUEST_DECLINATION);
        publishPushNotificationEvent(joiner, MEET_JOIN_REQUEST_CONFIRMATION, null);

        meetJoinRequestsRepository.forceDelete(request);
    }

    @Transactional
    public void declineJoinRequest(Long requestId) {
        MeetJoinRequestJpa request = meetJoinRequestsRepository.fetch(requestId);
        Long authorId = getUserId();
        meetOperationsValidator.validateJoinRequestDeclination(request, authorId);

        MeetJpa meet = request.getMeet();
        UserJpa user = request.getUser();

        notificationService.notify(meet, user, MEET_JOIN_REQUEST_DECLINATION);
        publishPushNotificationEvent(user, MEET_JOIN_REQUEST_DECLINATION, null);

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

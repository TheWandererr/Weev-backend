package com.pivo.weev.backend.domain.service.moderation;

import static com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetStatus.CONFIRMED;
import static com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetStatus.DECLINED;
import static com.pivo.weev.backend.domain.utils.AuthUtils.getUserId;
import static com.pivo.weev.backend.domain.utils.Constants.NotificationDetails.DECLINATION_REASON;
import static com.pivo.weev.backend.domain.utils.Constants.NotificationTopics.MEET_CONFIRMATION;
import static com.pivo.weev.backend.domain.utils.Constants.NotificationTopics.MEET_DECLINATION;
import static com.pivo.weev.backend.domain.utils.Constants.NotificationTopics.MEET_UPDATE_FAILED;
import static com.pivo.weev.backend.domain.utils.Constants.NotificationTopics.MEET_UPDATE_SUCCESSFUL;
import static com.pivo.weev.backend.utils.CollectionUtils.mapToList;
import static org.mapstruct.factory.Mappers.getMapper;

import com.pivo.weev.backend.domain.mapping.jpa.MeetJpaMapper;
import com.pivo.weev.backend.domain.model.event.PushNotificationEvent;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.DeclinationReasonJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserJpa;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.DeclinationReasonsRepository;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.MeetRepository;
import com.pivo.weev.backend.domain.service.event.factory.ApplicationEventFactory;
import com.pivo.weev.backend.domain.service.meet.MeetPhotoService;
import com.pivo.weev.backend.domain.service.message.NotificationService;
import com.pivo.weev.backend.domain.service.validation.ModerationValidator;
import com.pivo.weev.backend.domain.service.websocket.ChatService;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ModerationService {

    private final MeetRepository meetRepository;
    private final DeclinationReasonsRepository declinationReasonsRepository;

    private final ModerationValidator moderationValidator;
    private final MeetPhotoService meetPhotoService;
    private final NotificationService notificationService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final ApplicationEventFactory applicationEventFactory;
    private final ChatService chatService;

    @Transactional
    public void confirmMeet(Long id) {
        MeetJpa confirmable = meetRepository.fetch(id);
        moderationValidator.validateConfirmation(confirmable);
        if (confirmable.hasUpdatableTarget()) {
            MeetJpa updatable = confirmable.getUpdatableTarget();
            confirmMeetUpdate(confirmable, updatable);
        } else {
            confirmNewMeet(confirmable);
        }
    }

    private void confirmNewMeet(MeetJpa confirmable) {
        confirmable.setModeratedBy(getUserId());
        confirmable.setStatus(CONFIRMED);
        UserJpa creator = confirmable.getCreator();
        notify(confirmable, creator, MEET_CONFIRMATION);
        chatService.createChat(creator, confirmable);
    }

    private void notify(MeetJpa meet, UserJpa recipient, String topic) {
        notificationService.notify(meet, recipient, topic);
        PushNotificationEvent event = applicationEventFactory.buildPushNotificationEvent(meet, recipient, topic);
        applicationEventPublisher.publishEvent(event);
    }

    private void notify(MeetJpa meet, UserJpa recipient, String topic, DeclinationReasonJpa declinationReasonJpa) {
        notificationService.notify(meet, recipient, topic, declinationReasonJpa);
        PushNotificationEvent event = applicationEventFactory.buildPushNotificationEvent(meet, recipient, topic, Map.of(DECLINATION_REASON, declinationReasonJpa.getTitle()));
        applicationEventPublisher.publishEvent(event);
    }

    private void notifyAll(MeetJpa meet, Set<UserJpa> recipients, String topic) {
        notificationService.notifyAll(meet, recipients, topic);
        PushNotificationEvent event = applicationEventFactory.buildPushNotificationEvent(meet, recipients, topic);
        applicationEventPublisher.publishEvent(event);
    }

    private void confirmMeetUpdate(MeetJpa confirmable, MeetJpa updatable) {
        meetPhotoService.deletePhoto(updatable);
        meetRepository.forceDelete(confirmable);

        getMapper(MeetJpaMapper.class).update(confirmable, updatable);
        updatable.setModeratedBy(getUserId());
        updatable.setStatus(CONFIRMED);

        Set<UserJpa> recipients = updatable.getMembersWithCreator();
        notifyAll(updatable, recipients, MEET_UPDATE_SUCCESSFUL);
    }

    public List<String> getDeclinationReasons() {
        return mapToList(declinationReasonsRepository.getAll(), DeclinationReasonJpa::getTitle);
    }

    @Transactional
    public void declineMeet(Long id, String declinationTitle) {
        DeclinationReasonJpa declinationReasonJpa = declinationReasonsRepository.fetchByTitle(declinationTitle);
        MeetJpa declinable = meetRepository.fetch(id);
        if (declinable.hasUpdatableTarget()) {
            MeetJpa updatableTarget = declinable.getUpdatableTarget();
            declineMeetUpdate(declinable, updatableTarget, declinationReasonJpa);
        } else {
            declineNewMeet(declinable, declinationReasonJpa);
        }
    }

    private void declineMeetUpdate(MeetJpa declinable, MeetJpa updatableTarget, DeclinationReasonJpa declinationReasonJpa) {
        meetPhotoService.deletePhoto(declinable);
        meetRepository.forceDelete(declinable);
        notify(updatableTarget, updatableTarget.getCreator(), MEET_UPDATE_FAILED, declinationReasonJpa);
    }

    private void declineNewMeet(MeetJpa declinable, DeclinationReasonJpa declinationReasonJpa) {
        declinable.setModeratedBy(getUserId());
        declinable.setStatus(DECLINED);
        notify(declinable, declinable.getCreator(), MEET_DECLINATION, declinationReasonJpa);
    }
}

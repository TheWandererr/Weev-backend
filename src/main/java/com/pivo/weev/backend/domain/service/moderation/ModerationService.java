package com.pivo.weev.backend.domain.service.moderation;

import static com.pivo.weev.backend.common.utils.CollectionUtils.mapToList;
import static com.pivo.weev.backend.domain.persistance.jpa.model.event.EventStatus.CONFIRMED;
import static com.pivo.weev.backend.domain.persistance.jpa.model.event.EventStatus.DECLINED;
import static com.pivo.weev.backend.domain.utils.AuthUtils.getUserId;
import static com.pivo.weev.backend.domain.utils.Constants.NotificationTitles.EVENT_CONFIRMATION;
import static com.pivo.weev.backend.domain.utils.Constants.NotificationTitles.EVENT_DECLINATION;
import static com.pivo.weev.backend.domain.utils.Constants.NotificationTitles.EVENT_UPDATE_FAILED;
import static com.pivo.weev.backend.domain.utils.Constants.NotificationTitles.EVENT_UPDATE_SUCCESSFUL;
import static org.mapstruct.factory.Mappers.getMapper;

import com.pivo.weev.backend.domain.mapping.jpa.EventJpaMapper;
import com.pivo.weev.backend.domain.persistance.jpa.NotificationFactory;
import com.pivo.weev.backend.domain.persistance.jpa.model.common.NotificationJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.event.DeclinationReason;
import com.pivo.weev.backend.domain.persistance.jpa.model.event.EventJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.event.EventNotificationJpa;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.DeclinationReasonsRepositoryWrapper;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.EventRepositoryWrapper;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.NotificationRepositoryWrapper;
import com.pivo.weev.backend.domain.service.event.EventsOperatingService;
import com.pivo.weev.backend.domain.service.validation.ModerationValidator;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ModerationService {

    private final EventRepositoryWrapper eventRepository;
    private final NotificationRepositoryWrapper notificationRepository;
    private final DeclinationReasonsRepositoryWrapper declinationReasonsRepository;

    private final ModerationValidator moderationValidator;
    private final NotificationFactory notificationFactory;
    private final EventsOperatingService eventsOperatingService;

    @Transactional
    public void confirmEvent(Long id) {
        EventJpa confirmable = eventRepository.fetch(id);
        moderationValidator.validateConfirmation(confirmable);
        if (confirmable.hasUpdatableTarget()) {
            EventJpa updatable = confirmable.getUpdatableTarget();
            confirmEventUpdate(confirmable, updatable);
        } else {
            confirmNewEvent(confirmable);
        }
    }

    private void confirmNewEvent(EventJpa confirmable) {
        confirmable.setModeratedBy(getUserId());
        confirmable.setStatus(CONFIRMED);
        EventNotificationJpa notification = notificationFactory.createEventNotification(confirmable, EVENT_CONFIRMATION);
        notificationRepository.save(notification);
    }

    private void confirmEventUpdate(EventJpa confirmable, EventJpa updatable) {

        eventsOperatingService.deletePhoto(updatable);

        getMapper(EventJpaMapper.class).map(confirmable, updatable);
        updatable.setModeratedBy(getUserId());
        updatable.setStatus(CONFIRMED);

        eventRepository.delete(confirmable);

        EventNotificationJpa notification = notificationFactory.createEventNotification(updatable, EVENT_UPDATE_SUCCESSFUL);
        notificationRepository.save(notification);
    }

    public List<String> getDeclinationReasons() {
        return mapToList(declinationReasonsRepository.getAll(), DeclinationReason::getTitle);
    }

    @Transactional
    public void declineEvent(Long id, String declinationTitle) {
        DeclinationReason declinationReason = declinationReasonsRepository.fetchByTitle(declinationTitle);
        EventJpa declinable = eventRepository.fetch(id);
        if (declinable.hasUpdatableTarget()) {
            EventJpa updatableTarget = declinable.getUpdatableTarget();
            declineEventUpdate(declinable, updatableTarget, declinationReason);
        } else {
            declineNewEvent(declinable, declinationReason);
        }
    }

    private void declineEventUpdate(EventJpa declinable, EventJpa updatableTarget, DeclinationReason declinationReason) {
        eventsOperatingService.deletePhoto(declinable);
        eventRepository.delete(declinable);
        NotificationJpa notification = notificationFactory.createEventNotification(updatableTarget, EVENT_UPDATE_FAILED, declinationReason);
        notificationRepository.save(notification);
    }

    private void declineNewEvent(EventJpa declinable, DeclinationReason declinationReason) {
        declinable.setModeratedBy(getUserId());
        declinable.setStatus(DECLINED);
        NotificationJpa notification = notificationFactory.createEventNotification(declinable, EVENT_DECLINATION, declinationReason);
        notificationRepository.save(notification);
    }
}

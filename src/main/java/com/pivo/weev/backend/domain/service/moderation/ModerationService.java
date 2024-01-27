package com.pivo.weev.backend.domain.service.moderation;

import static com.pivo.weev.backend.domain.persistance.jpa.model.event.EventStatus.CONFIRMED;
import static com.pivo.weev.backend.domain.persistance.jpa.model.event.EventStatus.DECLINED;
import static com.pivo.weev.backend.domain.utils.AuthUtils.getUserId;
import static com.pivo.weev.backend.domain.utils.Constants.NotificationTitles.EVENT_CONFIRMATION;
import static com.pivo.weev.backend.domain.utils.Constants.NotificationTitles.EVENT_DECLINATION;
import static com.pivo.weev.backend.domain.utils.Constants.NotificationTitles.EVENT_UPDATE_FAILED;
import static com.pivo.weev.backend.domain.utils.Constants.NotificationTitles.EVENT_UPDATE_SUCCESSFUL;
import static com.pivo.weev.backend.utils.CollectionUtils.mapToList;
import static org.mapstruct.factory.Mappers.getMapper;

import com.pivo.weev.backend.domain.mapping.jpa.EventJpaMapper;
import com.pivo.weev.backend.domain.persistance.jpa.model.event.DeclinationReason;
import com.pivo.weev.backend.domain.persistance.jpa.model.event.EventJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserJpa;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.DeclinationReasonsRepositoryWrapper;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.EventsRepositoryWrapper;
import com.pivo.weev.backend.domain.service.NotificationService;
import com.pivo.weev.backend.domain.service.event.EventsPhotoService;
import com.pivo.weev.backend.domain.service.validation.ModerationValidator;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ModerationService {

    private final EventsRepositoryWrapper eventsRepository;
    private final DeclinationReasonsRepositoryWrapper declinationReasonsRepository;

    private final ModerationValidator moderationValidator;
    private final EventsPhotoService eventsPhotoService;
    private final NotificationService notificationService;

    @Transactional
    public void confirmEvent(Long id) {
        EventJpa confirmable = eventsRepository.fetch(id);
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
        notificationService.notify(confirmable, confirmable.getCreator(), EVENT_CONFIRMATION);
    }

    private void confirmEventUpdate(EventJpa confirmable, EventJpa updatable) {
        eventsPhotoService.deletePhoto(updatable);
        eventsRepository.forceDelete(confirmable);

        getMapper(EventJpaMapper.class).remap(confirmable, updatable);
        updatable.setModeratedBy(getUserId());
        updatable.setStatus(CONFIRMED);

        Set<UserJpa> recipients = updatable.getMembersWithCreator();
        notificationService.notifyAll(recipients, updatable, EVENT_UPDATE_SUCCESSFUL);
    }

    public List<String> getDeclinationReasons() {
        return mapToList(declinationReasonsRepository.getAll(), DeclinationReason::getTitle);
    }

    @Transactional
    public void declineEvent(Long id, String declinationTitle) {
        DeclinationReason declinationReason = declinationReasonsRepository.fetchByTitle(declinationTitle);
        EventJpa declinable = eventsRepository.fetch(id);
        if (declinable.hasUpdatableTarget()) {
            EventJpa updatableTarget = declinable.getUpdatableTarget();
            declineEventUpdate(declinable, updatableTarget, declinationReason);
        } else {
            declineNewEvent(declinable, declinationReason);
        }
    }

    private void declineEventUpdate(EventJpa declinable, EventJpa updatableTarget, DeclinationReason declinationReason) {
        eventsPhotoService.deletePhoto(declinable);
        eventsRepository.forceDelete(declinable);
        notificationService.notify(updatableTarget, updatableTarget.getCreator(), EVENT_UPDATE_FAILED, declinationReason);
    }

    private void declineNewEvent(EventJpa declinable, DeclinationReason declinationReason) {
        declinable.setModeratedBy(getUserId());
        declinable.setStatus(DECLINED);
        notificationService.notify(declinable, declinable.getCreator(), EVENT_DECLINATION, declinationReason);
    }
}

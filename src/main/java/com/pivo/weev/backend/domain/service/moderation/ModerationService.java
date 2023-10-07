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
import com.pivo.weev.backend.domain.persistance.jpa.model.event.DeclinationReason;
import com.pivo.weev.backend.domain.persistance.jpa.model.event.EventJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserJpa;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.DeclinationReasonsRepositoryWrapper;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.EventRepositoryWrapper;
import com.pivo.weev.backend.domain.service.NotificationService;
import com.pivo.weev.backend.domain.service.event.EventImageService;
import com.pivo.weev.backend.domain.service.validation.ModerationValidator;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ModerationService {

    private final EventRepositoryWrapper eventRepository;
    private final DeclinationReasonsRepositoryWrapper declinationReasonsRepository;

    private final ModerationValidator moderationValidator;
    private final EventImageService eventImageService;
    private final NotificationService notificationService;

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
        notificationService.notify(confirmable, confirmable.getCreator(), EVENT_CONFIRMATION);
    }

    private void confirmEventUpdate(EventJpa confirmable, EventJpa updatable) {
        eventImageService.deletePhoto(updatable);

        getMapper(EventJpaMapper.class).remap(confirmable, updatable);
        updatable.setModeratedBy(getUserId());
        updatable.setStatus(CONFIRMED);

        eventRepository.forceDelete(confirmable);

        Set<UserJpa> recipients = updatable.getUsers();
        notificationService.notifyAll(recipients, updatable, EVENT_UPDATE_SUCCESSFUL);
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
        eventImageService.deletePhoto(declinable);
        eventRepository.forceDelete(declinable);
        notificationService.notify(updatableTarget, updatableTarget.getCreator(), EVENT_UPDATE_FAILED, declinationReason);
    }

    private void declineNewEvent(EventJpa declinable, DeclinationReason declinationReason) {
        declinable.setModeratedBy(getUserId());
        declinable.setStatus(DECLINED);
        notificationService.notify(declinable, declinable.getCreator(), EVENT_DECLINATION, declinationReason);
    }
}

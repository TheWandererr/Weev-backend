package com.pivo.weev.backend.domain.service.moderation;

import static com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetStatus.CONFIRMED;
import static com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetStatus.DECLINED;
import static com.pivo.weev.backend.domain.utils.AuthUtils.getUserId;
import static com.pivo.weev.backend.domain.utils.Constants.NotificationTitles.MEET_CONFIRMATION;
import static com.pivo.weev.backend.domain.utils.Constants.NotificationTitles.MEET_DECLINATION;
import static com.pivo.weev.backend.domain.utils.Constants.NotificationTitles.MEET_UPDATE_FAILED;
import static com.pivo.weev.backend.domain.utils.Constants.NotificationTitles.MEET_UPDATE_SUCCESSFUL;
import static com.pivo.weev.backend.utils.CollectionUtils.mapToList;
import static org.mapstruct.factory.Mappers.getMapper;

import com.pivo.weev.backend.domain.mapping.jpa.MeetJpaMapper;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.DeclinationReasonJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserJpa;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.DeclinationReasonsRepositoryWrapper;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.MeetRepositoryWrapper;
import com.pivo.weev.backend.domain.service.NotificationService;
import com.pivo.weev.backend.domain.service.meet.MeetPhotoService;
import com.pivo.weev.backend.domain.service.validation.ModerationValidator;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ModerationService {

    private final MeetRepositoryWrapper meetRepository;
    private final DeclinationReasonsRepositoryWrapper declinationReasonsRepository;

    private final ModerationValidator moderationValidator;
    private final MeetPhotoService meetPhotoService;
    private final NotificationService notificationService;

    @Transactional
    public void confirmEvent(Long id) {
        MeetJpa confirmable = meetRepository.fetch(id);
        moderationValidator.validateConfirmation(confirmable);
        if (confirmable.hasUpdatableTarget()) {
            MeetJpa updatable = confirmable.getUpdatableTarget();
            confirmEventUpdate(confirmable, updatable);
        } else {
            confirmNewEvent(confirmable);
        }
    }

    private void confirmNewEvent(MeetJpa confirmable) {
        confirmable.setModeratedBy(getUserId());
        confirmable.setStatus(CONFIRMED);
        notificationService.notify(confirmable, confirmable.getCreator(), MEET_CONFIRMATION);
    }

    private void confirmEventUpdate(MeetJpa confirmable, MeetJpa updatable) {
        meetPhotoService.deletePhoto(updatable);
        meetRepository.forceDelete(confirmable);

        getMapper(MeetJpaMapper.class).remap(confirmable, updatable);
        updatable.setModeratedBy(getUserId());
        updatable.setStatus(CONFIRMED);

        Set<UserJpa> recipients = updatable.getMembersWithCreator();
        notificationService.notifyAll(updatable, recipients, MEET_UPDATE_SUCCESSFUL);
    }

    public List<String> getDeclinationReasons() {
        return mapToList(declinationReasonsRepository.getAll(), DeclinationReasonJpa::getTitle);
    }

    @Transactional
    public void declineEvent(Long id, String declinationTitle) {
        DeclinationReasonJpa declinationReasonJpa = declinationReasonsRepository.fetchByTitle(declinationTitle);
        MeetJpa declinable = meetRepository.fetch(id);
        if (declinable.hasUpdatableTarget()) {
            MeetJpa updatableTarget = declinable.getUpdatableTarget();
            declineEventUpdate(declinable, updatableTarget, declinationReasonJpa);
        } else {
            declineNewEvent(declinable, declinationReasonJpa);
        }
    }

    private void declineEventUpdate(MeetJpa declinable, MeetJpa updatableTarget, DeclinationReasonJpa declinationReasonJpa) {
        meetPhotoService.deletePhoto(declinable);
        meetRepository.forceDelete(declinable);
        notificationService.notify(updatableTarget, updatableTarget.getCreator(), MEET_UPDATE_FAILED, declinationReasonJpa);
    }

    private void declineNewEvent(MeetJpa declinable, DeclinationReasonJpa declinationReasonJpa) {
        declinable.setModeratedBy(getUserId());
        declinable.setStatus(DECLINED);
        notificationService.notify(declinable, declinable.getCreator(), MEET_DECLINATION, declinationReasonJpa);
    }
}

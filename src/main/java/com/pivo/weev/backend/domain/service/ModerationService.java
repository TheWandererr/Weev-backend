package com.pivo.weev.backend.domain.service;

import static com.pivo.weev.backend.domain.persistance.jpa.model.event.EventStatus.CONFIRMED;
import static com.pivo.weev.backend.domain.utils.AuthUtils.getUserId;
import static org.mapstruct.factory.Mappers.getMapper;

import com.pivo.weev.backend.domain.mapping.EventJpaMapper;
import com.pivo.weev.backend.domain.persistance.jpa.model.event.EventJpa;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.EventRepositoryWrapper;
import com.pivo.weev.backend.domain.service.validation.ModerationValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ModerationService {

    private final EventRepositoryWrapper eventRepository;
    private final ModerationValidator moderationValidator;

    @Transactional
    public void confirmEvent(Long id) {
        EventJpa confirmable = eventRepository.fetch(id);
        moderationValidator.validateConfirmation(confirmable);
        if (confirmable.hasUpdatableTarget()) {
            EventJpa updatable = confirmable.getUpdatableTarget();
            getMapper(EventJpaMapper.class).map(confirmable, updatable);
            updatable.setModeratedBy(getUserId());
            updatable.setEventStatus(CONFIRMED);
            eventRepository.delete(confirmable);
        } else {
            confirmable.setModeratedBy(getUserId());
            confirmable.setEventStatus(CONFIRMED);
        }
    }
}

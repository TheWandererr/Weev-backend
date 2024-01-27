package com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper;

import static com.pivo.weev.backend.domain.persistance.jpa.model.common.ResourceName.EVENT_REQUEST;

import com.pivo.weev.backend.domain.persistance.jpa.model.event.EventRequestJpa;
import com.pivo.weev.backend.domain.persistance.jpa.repository.IEventRequestsRepository;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class EventRequestsRepositoryWrapper extends GenericRepositoryWrapper<Long, EventRequestJpa, IEventRequestsRepository> {

    protected EventRequestsRepositoryWrapper(IEventRequestsRepository repository) {
        super(repository, EVENT_REQUEST);
    }

    public Optional<EventRequestJpa> findByEventIdAndUserId(Long eventId, Long userId) {
        return repository.findByEventIdAndUserId(eventId, userId);
    }
}

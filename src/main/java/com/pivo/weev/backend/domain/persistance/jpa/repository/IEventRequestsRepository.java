package com.pivo.weev.backend.domain.persistance.jpa.repository;

import com.pivo.weev.backend.domain.persistance.jpa.model.event.EventRequestJpa;
import java.util.Optional;

public interface IEventRequestsRepository extends IGenericRepository<Long, EventRequestJpa> {

    Optional<EventRequestJpa> findByEventIdAndUserId(Long eventId, Long userId);
}

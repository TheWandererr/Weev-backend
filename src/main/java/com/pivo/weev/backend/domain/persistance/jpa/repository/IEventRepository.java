package com.pivo.weev.backend.domain.persistance.jpa.repository;

import com.pivo.weev.backend.domain.persistance.jpa.model.event.EventJpa;
import java.util.Optional;

public interface IEventRepository extends IGenericRepository<Long, EventJpa> {

     Optional<EventJpa> findByUpdatableTargetId(Long id);
}

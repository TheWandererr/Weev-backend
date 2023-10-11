package com.pivo.weev.backend.domain.persistance.jpa.repository;

import com.pivo.weev.backend.domain.persistance.jpa.model.event.EventJpa;
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface IEventRepository extends IGenericRepository<Long, EventJpa> {

    @Query(value = "select * from events e where e.updatable_event_id =?1", nativeQuery = true)
    Optional<EventJpa> findByUpdatableTargetId(Long id);

    @Modifying
    @Query(value = "delete from events e where e.updatable_event_id = ?1", nativeQuery = true)
    void deleteByUpdatableTargetId(Long id);

    @Modifying
    @Query(value = "delete from EventJpa e where e.id = ?1")
    void deleteById(Long id);

    Optional<EventJpa> findByIdAndDeletedIsFalse(Long id);
}

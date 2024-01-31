package com.pivo.weev.backend.domain.persistance.jpa.repository;

import com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetJpa;
import java.util.Optional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface IMeetRepository extends IGenericRepository<Long, MeetJpa> {

    @Query(value = "select * from meets m where m.updatable_meet_id =?1", nativeQuery = true)
    Optional<MeetJpa> findByUpdatableTargetId(Long id);

    @Modifying
    @Query(value = "delete from meets m where m.updatable_meet_id = ?1", nativeQuery = true)
    void deleteByUpdatableTargetId(Long id);

    @Modifying
    @Query(value = "delete from MeetJpa e where e.id = ?1")
    void deleteById(Long id);
}

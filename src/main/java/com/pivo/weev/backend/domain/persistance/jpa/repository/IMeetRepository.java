package com.pivo.weev.backend.domain.persistance.jpa.repository;

import com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetStatus;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
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
    @Query(value = "delete from meets m where m.updatable_meet_id in ?1", nativeQuery = true)
    void deleteAllByUpdatableTargetId(Collection<Long> id);

    @Modifying
    @Query(value = "delete from MeetJpa e where e.id = ?1")
    void deleteById(Long id);

    @Query("select m from MeetJpa m where m.creator.id = ?1 and m.utcEndDateTime > ?2 and m.status in ?3")
    List<MeetJpa> findAllByCreator_IdAndUtcEndDateTimeAfterAndStatusIn(Long id, Instant now, Collection<MeetStatus> statuses);

    @Query(value = "select m.* from meets m inner join meet_members mb on mb.meet_id = m.id where mb.user_id = ?1 and m.utc_end_date_time > ?2", nativeQuery = true)
    List<MeetJpa> findAllByMember_IdAndUtcEndDateTimeAfter(Long id, Instant now);
}

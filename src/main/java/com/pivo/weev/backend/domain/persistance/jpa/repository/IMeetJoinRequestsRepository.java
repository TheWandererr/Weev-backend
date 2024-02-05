package com.pivo.weev.backend.domain.persistance.jpa.repository;

import com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetJoinRequestJpa;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IMeetJoinRequestsRepository extends IGenericRepository<Long, MeetJoinRequestJpa> {

    Optional<MeetJoinRequestJpa> findByMeetIdAndUserId(Long meetId, Long userId);

    Page<MeetJoinRequestJpa> findAllByMeet_Id(Long meetId, Pageable pageable);

    boolean existsByMeetIdAndUserId(Long meetId, Long userId);

    void deleteAllByUserId(Long userId);
}

package com.pivo.weev.backend.domain.persistance.jpa.repository;

import com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetRequestJpa;
import java.util.Optional;

public interface IMeetRequestsRepository extends IGenericRepository<Long, MeetRequestJpa> {

    Optional<MeetRequestJpa> findByMeetIdAndUserId(Long meetId, Long userId);
}

package com.pivo.weev.backend.domain.persistance.jpa.repository;

import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserJpa;
import com.pivo.weev.backend.domain.persistance.jpa.projection.MeetsStatisticsProjection;
import org.springframework.data.jpa.repository.Query;

public interface IUsersRepository extends IGenericRepository<Long, UserJpa> {

    @Query(value = "SELECT (SELECT count(m.creator_id) from meets m where creator_id = ?1) as created, (SELECT count(mm.user_id) from meet_members mm where user_id = ?1) as participated", nativeQuery = true)
    MeetsStatisticsProjection getMeetsStatistics(Long userId);
}

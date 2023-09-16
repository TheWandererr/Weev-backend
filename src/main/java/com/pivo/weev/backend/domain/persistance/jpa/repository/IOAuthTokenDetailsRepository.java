package com.pivo.weev.backend.domain.persistance.jpa.repository;

import com.pivo.weev.backend.domain.persistance.jpa.model.auth.OAuthTokenDetailsJpa;
import java.time.Instant;
import java.util.List;

public interface IOAuthTokenDetailsRepository extends IGenericRepository<Long, OAuthTokenDetailsJpa> {

    OAuthTokenDetailsJpa findByUserIdAndDeviceId(Long userId, String deviceId);

    List<OAuthTokenDetailsJpa> findAllByExpiresAtBefore(Instant instant);

    void deleteByUserIdAndDeviceId(Long userId, String deviceId);
}

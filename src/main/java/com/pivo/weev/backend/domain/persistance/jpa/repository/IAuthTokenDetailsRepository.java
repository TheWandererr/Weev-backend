package com.pivo.weev.backend.domain.persistance.jpa.repository;

import com.pivo.weev.backend.domain.persistance.jpa.model.auth.AuthTokensDetailsJpa;
import java.time.Instant;
import java.util.List;

public interface IAuthTokenDetailsRepository extends IGenericRepository<Long, AuthTokensDetailsJpa> {

    AuthTokensDetailsJpa findByUserIdAndDeviceId(Long userId, String deviceId);

    List<AuthTokensDetailsJpa> findAllByExpiresAtBefore(Instant instant);

    void deleteByUserIdAndDeviceId(Long userId, String deviceId);
}

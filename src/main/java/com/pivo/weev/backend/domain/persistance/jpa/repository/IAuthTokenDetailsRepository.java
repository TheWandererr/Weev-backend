package com.pivo.weev.backend.domain.persistance.jpa.repository;

import com.pivo.weev.backend.domain.persistance.jpa.model.auth.AuthTokensDetailsJpa;
import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface IAuthTokenDetailsRepository extends IGenericRepository<Long, AuthTokensDetailsJpa> {

    @Query("SELECT td FROM AuthTokensDetailsJpa td WHERE td.device.user.id = ?1 AND td.device.internalId = ?2")
    AuthTokensDetailsJpa findByUserIdAndDeviceInternalId(Long userId, String deviceId);

    List<AuthTokensDetailsJpa> findAllByExpiresAtBefore(Instant instant);

    @Modifying
    @Query("DELETE FROM AuthTokensDetailsJpa td WHERE td.device.user.id = ?1 AND td.device.internalId = ?2")
    void deleteByUserIdAndDeviceInternalId(Long userId, String deviceId);

    @Modifying
    @Query("DELETE FROM AuthTokensDetailsJpa td WHERE td.device.user.id = ?1")
    void deleteAllByUserId(Long userId);
}

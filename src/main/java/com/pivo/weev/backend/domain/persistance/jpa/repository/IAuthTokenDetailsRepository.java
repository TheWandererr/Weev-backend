package com.pivo.weev.backend.domain.persistance.jpa.repository;

import com.pivo.weev.backend.domain.persistance.jpa.model.auth.AuthTokensDetailsJpa;
import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface IAuthTokenDetailsRepository extends IGenericRepository<Long, AuthTokensDetailsJpa> {

    @Query(value = "SELECT td.* FROM auth_tokens_details td INNER JOIN devices d ON d.id = td.device_id WHERE d.user_id = ?1 AND d.internal_id = ?2" , nativeQuery = true)
    AuthTokensDetailsJpa findByUserIdAndDeviceInternalId(Long userId, String deviceId);

    List<AuthTokensDetailsJpa> findAllByExpiresAtBefore(Instant instant);

    @Modifying
    @Query(value = "DELETE FROM AuthTokensDetailsJpa th WHERE th.device.user.id = ?1 AND th.device.internalId = ?2")
    void deleteByUserIdAndDeviceInternalId(Long userId, String deviceId);

    @Modifying
    @Query("DELETE FROM AuthTokensDetailsJpa td WHERE td.device.user.id = ?1")
    void deleteAllByUserId(Long userId);
}

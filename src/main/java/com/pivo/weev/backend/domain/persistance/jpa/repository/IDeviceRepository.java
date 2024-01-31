package com.pivo.weev.backend.domain.persistance.jpa.repository;

import com.pivo.weev.backend.domain.persistance.jpa.model.user.DeviceJpa;
import java.util.Optional;

public interface IDeviceRepository extends IGenericRepository<Long, DeviceJpa> {

    Optional<DeviceJpa> findByUserIdAndInternalId(Long userId, String internalId);
}

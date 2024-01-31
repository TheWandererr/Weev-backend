package com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper;

import static com.pivo.weev.backend.domain.persistance.jpa.model.common.ResourceName.DEVICE;

import com.pivo.weev.backend.domain.persistance.jpa.model.user.DeviceJpa;
import com.pivo.weev.backend.domain.persistance.jpa.repository.IDeviceRepository;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class DeviceRepositoryWrapper extends GenericRepositoryWrapper<Long, DeviceJpa, IDeviceRepository> {

    protected DeviceRepositoryWrapper(IDeviceRepository repository) {
        super(repository, DEVICE);
    }

    public Optional<DeviceJpa> findByUserIdAndInternalId(Long userId, String internalId) {
        return repository.findByUserIdAndInternalId(userId, internalId);
    }
}

package com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper;

import static com.pivo.weev.backend.domain.persistance.jpa.model.common.ResourceName.DEVICE;

import com.pivo.weev.backend.domain.persistance.jpa.exception.ResourceNotFoundException;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.DeviceJpa;
import com.pivo.weev.backend.domain.persistance.jpa.repository.IDeviceRepository;
import java.util.Optional;
import org.springframework.stereotype.Component;

@Component
public class DeviceRepository extends GenericRepository<Long, DeviceJpa, IDeviceRepository> {

    protected DeviceRepository(IDeviceRepository repository) {
        super(repository, DEVICE);
    }

    public Optional<DeviceJpa> findByUserIdAndInternalId(Long userId, String internalId) {
        return repository.findByUserIdAndInternalId(userId, internalId);
    }

    public DeviceJpa fetchByUserIdAndInternalId(Long userId, String internalId) {
        return findByUserIdAndInternalId(userId, internalId)
                .orElseThrow(() -> new ResourceNotFoundException(notFound()));
    }
}

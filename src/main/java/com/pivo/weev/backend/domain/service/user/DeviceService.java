package com.pivo.weev.backend.domain.service.user;

import static com.pivo.weev.backend.utils.LocaleUtils.getAcceptedLanguage;
import static org.mapstruct.factory.Mappers.getMapper;

import com.pivo.weev.backend.domain.mapping.jpa.DeviceJpaMapper;
import com.pivo.weev.backend.domain.model.user.Device;
import com.pivo.weev.backend.domain.model.user.Device.Settings;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.DeviceJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserJpa;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.DeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeviceService {

    private final DeviceRepository deviceRepository;

    public DeviceJpa resolveDevice(UserJpa user, String deviceId) {
        return deviceRepository.findByUserIdAndInternalId(user.getId(), deviceId)
                               .orElseGet(() -> deviceRepository.save(new DeviceJpa(user, deviceId, getAcceptedLanguage())));
    }

    @Transactional
    public Settings updateDeviceSettings(Device device) {
        DeviceJpa deviceJpa = deviceRepository.fetchByUserIdAndInternalId(device.getUserId(), device.getId());
        getMapper(DeviceJpaMapper.class).map(device, deviceJpa);
        return device.getSettings();
    }
}

package com.pivo.weev.backend.domain.service.user;

import static com.pivo.weev.backend.utils.LocaleUtils.getAcceptedLanguage;

import com.pivo.weev.backend.domain.persistance.jpa.model.user.DeviceJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserJpa;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.DeviceRepositoryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeviceService {

    private final DeviceRepositoryWrapper deviceRepository;

    public DeviceJpa resolveDevice(UserJpa user, String deviceId) {
        return deviceRepository.findByUserIdAndInternalId(user.getId(), deviceId)
                               .orElseGet(() -> deviceRepository.save(new DeviceJpa(user, deviceId, getAcceptedLanguage())));
    }
}

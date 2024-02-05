package com.pivo.weev.backend.domain.mapping.domain;

import com.pivo.weev.backend.domain.model.user.Device;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.DeviceJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.DeviceSettingsJpa;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface DeviceMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "settings", source = "settings")
    Device map(DeviceJpa source);

    Device.Settings map(DeviceSettingsJpa source);
}

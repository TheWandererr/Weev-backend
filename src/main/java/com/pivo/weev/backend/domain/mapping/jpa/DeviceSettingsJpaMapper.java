package com.pivo.weev.backend.domain.mapping.jpa;

import com.pivo.weev.backend.domain.model.user.Device.Settings;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.DeviceSettingsJpa;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper
public interface DeviceSettingsJpaMapper {

    void map(Settings source, @MappingTarget DeviceSettingsJpa destination);
}

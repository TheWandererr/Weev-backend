package com.pivo.weev.backend.domain.mapping.domain;

import com.pivo.weev.backend.domain.model.event.payload.DevicePayload;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.DeviceJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.DeviceSettingsJpa;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface DevicePayloadMapper {

    @Mapping(target = "settings", source = "settings")
    DevicePayload map(DeviceJpa source);
    
    DevicePayload.Settings map(DeviceSettingsJpa source);
}

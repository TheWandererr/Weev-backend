package com.pivo.weev.backend.rest.mapping.rest;

import com.pivo.weev.backend.domain.model.user.Device.Settings;
import com.pivo.weev.backend.rest.model.user.DeviceSettingsRest;
import org.mapstruct.Mapper;

@Mapper
public interface DeviceSettingsRestMapper {

    DeviceSettingsRest map(Settings source);
}

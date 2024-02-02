package com.pivo.weev.backend.rest.mapping.domain;

import com.pivo.weev.backend.domain.model.user.Device;
import com.pivo.weev.backend.rest.model.request.DeviceSettingUpdateRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface DeviceMapper {

    @Mapping(target = "id", source = "deviceId")
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "settings", source = "source.settings")
    Device map(DeviceSettingUpdateRequest source, String deviceId, Long userId);
}

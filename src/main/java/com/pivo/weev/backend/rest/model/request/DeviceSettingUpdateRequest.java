package com.pivo.weev.backend.rest.model.request;

import com.pivo.weev.backend.rest.model.user.DeviceSettingsRest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeviceSettingUpdateRequest {

    private DeviceSettingsRest settings;
}

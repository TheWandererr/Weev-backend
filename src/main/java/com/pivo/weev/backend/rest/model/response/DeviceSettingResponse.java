package com.pivo.weev.backend.rest.model.response;

import com.pivo.weev.backend.rest.model.user.DeviceSettingsRest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DeviceSettingResponse {

    private DeviceSettingsRest settings;

}

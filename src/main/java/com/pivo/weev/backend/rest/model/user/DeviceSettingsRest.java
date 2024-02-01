package com.pivo.weev.backend.rest.model.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeviceSettingsRest {

    private String pushNotificationToken;
    private String lang;
}

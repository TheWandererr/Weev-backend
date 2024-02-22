package com.pivo.weev.backend.domain.model.messaging.payload;

import static java.util.Objects.isNull;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserPayload {

    private long id;
    private String nickname;
    private String avatarUrl;
    private List<DevicePayload> devices;

    public List<DevicePayload> getDevices() {
        if (isNull(devices)) {
            devices = new ArrayList<>();
        }
        return devices;
    }
}

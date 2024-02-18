package com.pivo.weev.backend.domain.model.event.payload;

import static java.util.Objects.isNull;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserPayload {

    private List<DevicePayload> devices;

    public List<DevicePayload> getDevices() {
        if (isNull(devices)) {
            devices = new ArrayList<>();
        }
        return devices;
    }
}

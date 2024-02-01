package com.pivo.weev.backend.domain.model.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Device {

    private String id;
    private Long userId;
    private Settings settings;

    @Getter
    @Setter
    public static final class Settings {

        private String pushNotificationToken;
        private String lang;
    }
}

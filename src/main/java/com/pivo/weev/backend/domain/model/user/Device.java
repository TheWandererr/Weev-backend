package com.pivo.weev.backend.domain.model.user;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Device {

    private String id;
    private Long userId;
    private Settings settings;

    public boolean hasPushNotificationToken() {
        return hasSettings() && isNotBlank(settings.getPushNotificationToken());
    }

    public boolean hasSettings() {
        return nonNull(settings);
    }

    public String getLang() {
        return hasSettings() ? getSettings().getLang() : null;
    }

    public String getPushNotificationToken() {
        return hasSettings() ? getSettings().getPushNotificationToken() : null;
    }

    @Getter
    @Setter
    public static final class Settings {

        private String pushNotificationToken;
        private String lang;
    }
}

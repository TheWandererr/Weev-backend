package com.pivo.weev.backend.domain.model.messaging.payload;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DevicePayload {

    private Long userId;
    private Settings settings;

    @Getter
    @Setter
    public static class Settings {

        private String pushNotificationToken;
        private String lang;

        public boolean hasPushNotificationToken() {
            return isNotBlank(pushNotificationToken);
        }
    }

    public boolean hasPushNotificationToken() {
        return hasSettings() && settings.hasPushNotificationToken();
    }

    private boolean hasSettings() {
        return nonNull(settings);
    }

    public String getLang() {
        return hasSettings() ? settings.getLang() : null;
    }

    public String getPushNotificationToken() {
        return hasSettings() ? settings.getPushNotificationToken() : null;
    }
}

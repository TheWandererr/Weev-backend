package com.pivo.weev.backend.integration.firebase.model.notification;

import static com.pivo.weev.backend.utils.CollectionUtils.isSingle;
import static java.util.Objects.isNull;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PushNotificationMessage {

    private String title;
    private String body;
    private Map<String, String> data;
    private Set<String> recipients;

    public boolean isMulticast() {
        return !isSingle(recipients);
    }

    public Set<String> getRecipients() {
        if (isNull(recipients)) {
            recipients = new HashSet<>();
        }
        return recipients;
    }

    public Map<String, String> getData() {
        if (isNull(data)) {
            data = new HashMap<>();
        }
        return data;
    }
}

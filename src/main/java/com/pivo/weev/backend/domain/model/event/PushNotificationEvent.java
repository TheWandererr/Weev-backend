package com.pivo.weev.backend.domain.model.event;

import static java.util.Objects.isNull;

import com.pivo.weev.backend.domain.model.event.payload.MeetPayload;
import com.pivo.weev.backend.domain.model.event.payload.UserPayload;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

public class PushNotificationEvent extends ApplicationEvent {

    public PushNotificationEvent(Object source) {
        super(source);
        if (!(source instanceof PushNotificationModel)) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public PushNotificationModel getSource() {
        return (PushNotificationModel) source;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PushNotificationModel {

        private MeetPayload meet;
        private Set<UserPayload> recipients;
        private String topic;
        private Map<String, Object> payload;

        public Map<String, Object> getPayload() {
            if (isNull(payload)) {
                payload = new HashMap<>();
            }
            return payload;
        }
    }
}

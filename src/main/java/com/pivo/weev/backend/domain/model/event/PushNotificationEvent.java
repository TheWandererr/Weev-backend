package com.pivo.weev.backend.domain.model.event;

import static java.util.Objects.isNull;

import com.pivo.weev.backend.domain.model.meet.Meet;
import com.pivo.weev.backend.domain.model.user.User;
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

        private Meet meet;
        private Set<User> recipients;
        private String topic;
        private Map<String, Object> details;

        public Map<String, Object> getDetails() {
            if (isNull(details)) {
                details = new HashMap<>();
            }
            return details;
        }
    }
}

package com.pivo.weev.backend.domain.service.event.model;

import com.pivo.weev.backend.domain.model.meet.Meet;
import com.pivo.weev.backend.domain.model.user.User;
import java.util.Map;
import java.util.Set;
import org.springframework.context.ApplicationEvent;

public class PushNotificationEvent extends ApplicationEvent {

    public PushNotificationEvent(Object source) {
        super(source);
        if (!(source instanceof PushNotificationModel)) {
            throw new IllegalArgumentException("push.notification.message.expected");
        }
    }

    @Override
    public PushNotificationModel getSource() {
        return (PushNotificationModel) source;
    }

    public record PushNotificationModel(Meet meet, Set<User> recipients, String topic, Map<String, Object> details) {

    }
}

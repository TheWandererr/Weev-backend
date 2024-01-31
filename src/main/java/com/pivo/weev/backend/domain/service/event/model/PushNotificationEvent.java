package com.pivo.weev.backend.domain.service.event.model;

import com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserJpa;
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

    public record PushNotificationModel(MeetJpa meet, Set<UserJpa> recipients, String title, Map<String, Object> details) {

    }
}

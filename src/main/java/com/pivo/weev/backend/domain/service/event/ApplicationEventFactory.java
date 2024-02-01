package com.pivo.weev.backend.domain.service.event;

import static java.util.Collections.singleton;

import com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserJpa;
import com.pivo.weev.backend.domain.service.event.model.PushNotificationEvent;
import com.pivo.weev.backend.domain.service.event.model.PushNotificationEvent.PushNotificationModel;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class ApplicationEventFactory {

    public PushNotificationEvent buildNotificationEvent(MeetJpa meet, Set<UserJpa> recipients, String topic, Map<String, Object> details) {
        PushNotificationModel pushNotificationModel = new PushNotificationModel(meet, recipients, topic, details);
        return new PushNotificationEvent(pushNotificationModel);
    }

    public PushNotificationEvent buildNotificationEvent(MeetJpa meet, Set<UserJpa> recipients, String topic) {
        return buildNotificationEvent(meet, recipients, topic, null);
    }

    public PushNotificationEvent buildNotificationEvent(MeetJpa meet, UserJpa recipient, String topic, Map<String, Object> details) {
        return buildNotificationEvent(meet, singleton(recipient), topic, details);
    }

    public PushNotificationEvent buildNotificationEvent(MeetJpa meet, UserJpa recipient, String topic) {
        return buildNotificationEvent(meet, singleton(recipient), topic, null);
    }
}

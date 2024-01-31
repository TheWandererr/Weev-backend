package com.pivo.weev.backend.domain.service.event;

import static java.util.Collections.singleton;

import com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserJpa;
import com.pivo.weev.backend.domain.service.event.message.PushNotificationMessage;
import com.pivo.weev.backend.domain.service.event.model.PushNotificationEvent;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class ApplicationEventFactory {

    public PushNotificationEvent buildNotificationEvent(MeetJpa meet, Set<UserJpa> recipients, String title, Map<String, Object> details) {
        PushNotificationMessage pushNotificationMessage = new PushNotificationMessage(meet, recipients, title, details);
        return new PushNotificationEvent(pushNotificationMessage);
    }

    public PushNotificationEvent buildNotificationEvent(MeetJpa meet, Set<UserJpa> recipients, String title) {
        return buildNotificationEvent(meet, recipients, title, null);
    }

    public PushNotificationEvent buildNotificationEvent(MeetJpa meet, UserJpa recipient, String title, Map<String, Object> details) {
        return buildNotificationEvent(meet, singleton(recipient), title, details);
    }

    public PushNotificationEvent buildNotificationEvent(MeetJpa meet, UserJpa recipient, String title) {
        return buildNotificationEvent(meet, singleton(recipient), title, null);
    }
}

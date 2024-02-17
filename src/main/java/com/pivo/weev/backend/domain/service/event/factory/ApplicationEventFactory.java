package com.pivo.weev.backend.domain.service.event.factory;

import static java.util.Collections.singleton;
import static org.mapstruct.factory.Mappers.getMapper;

import com.pivo.weev.backend.domain.mapping.domain.MeetMapper;
import com.pivo.weev.backend.domain.mapping.domain.UserMapper;
import com.pivo.weev.backend.domain.model.event.PushNotificationEvent;
import com.pivo.weev.backend.domain.model.event.WebSocketEvent;
import com.pivo.weev.backend.domain.model.event.WebSocketEvent.EventType;
import com.pivo.weev.backend.domain.model.meet.Meet;
import com.pivo.weev.backend.domain.model.messaging.Chat;
import com.pivo.weev.backend.domain.model.user.User;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserJpa;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class ApplicationEventFactory {

    public PushNotificationEvent buildPushNotificationEvent(MeetJpa meetJpa, Set<UserJpa> recipientsJpa, String topic, Map<String, Object> details) {
        Meet meet = getMapper(MeetMapper.class).map(meetJpa);
        Set<User> recipients = getMapper(UserMapper.class).map(recipientsJpa);
        PushNotificationEvent.PushNotificationModel pushNotificationModel = new PushNotificationEvent.PushNotificationModel(meet, recipients, topic, details);
        return new PushNotificationEvent(pushNotificationModel);
    }

    public PushNotificationEvent buildPushNotificationEvent(MeetJpa meetJpa, Set<UserJpa> recipientsJpa, String topic) {
        return buildPushNotificationEvent(meetJpa, recipientsJpa, topic, null);
    }

    public PushNotificationEvent buildPushNotificationEvent(MeetJpa meetJpa, UserJpa recipientJpa, String topic, Map<String, Object> details) {
        return buildPushNotificationEvent(meetJpa, singleton(recipientJpa), topic, details);
    }

    public PushNotificationEvent buildPushNotificationEvent(MeetJpa meetJpa, UserJpa recipientJpa, String topic) {
        return buildPushNotificationEvent(meetJpa, singleton(recipientJpa), topic, null);
    }

    public WebSocketEvent buildWebSocketEvent(Chat chat, String recipient, EventType eventType) {
        WebSocketEvent.WebSocketMessageModel model = new WebSocketEvent.WebSocketMessageModel(chat, recipient, eventType);
        return new WebSocketEvent(model);
    }
}

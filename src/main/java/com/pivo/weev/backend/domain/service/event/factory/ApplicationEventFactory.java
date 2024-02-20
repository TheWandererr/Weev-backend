package com.pivo.weev.backend.domain.service.event.factory;

import static java.util.Collections.singleton;
import static org.mapstruct.factory.Mappers.getMapper;

import com.pivo.weev.backend.domain.mapping.domain.MeetPayloadMapper;
import com.pivo.weev.backend.domain.mapping.domain.UserPayloadMapper;
import com.pivo.weev.backend.domain.model.event.PushNotificationEvent;
import com.pivo.weev.backend.domain.model.event.WebSocketEvent;
import com.pivo.weev.backend.domain.model.event.WebSocketEvent.EventType;
import com.pivo.weev.backend.domain.model.event.payload.MeetPayload;
import com.pivo.weev.backend.domain.model.event.payload.UserPayload;
import com.pivo.weev.backend.domain.model.messaging.chat.ChatSnapshot;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.MeetJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserJpa;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class ApplicationEventFactory {

    public PushNotificationEvent buildPushNotificationEvent(MeetJpa meetJpa, Set<UserJpa> recipientsJpa, String topic, Map<String, Object> payload) {
        MeetPayload meet = getMapper(MeetPayloadMapper.class).map(meetJpa);
        Set<UserPayload> recipients = getMapper(UserPayloadMapper.class).map(recipientsJpa);
        PushNotificationEvent.PushNotificationModel pushNotificationModel = new PushNotificationEvent.PushNotificationModel(meet, recipients, topic, payload);
        return new PushNotificationEvent(pushNotificationModel);
    }

    public PushNotificationEvent buildPushNotificationEvent(MeetJpa meetJpa, Set<UserJpa> recipientsJpa, String topic) {
        return buildPushNotificationEvent(meetJpa, recipientsJpa, topic, null);
    }

    public PushNotificationEvent buildPushNotificationEvent(MeetJpa meetJpa, UserJpa recipientJpa, String topic, Map<String, Object> payload) {
        return buildPushNotificationEvent(meetJpa, singleton(recipientJpa), topic, payload);
    }

    public PushNotificationEvent buildPushNotificationEvent(MeetJpa meetJpa, UserJpa recipientJpa, String topic) {
        return buildPushNotificationEvent(meetJpa, singleton(recipientJpa), topic, null);
    }

    public WebSocketEvent buildWebSocketEvent(ChatSnapshot chatSnapshot, String recipient, EventType eventType) {
        WebSocketEvent.WebSocketMessageModel model = new WebSocketEvent.WebSocketMessageModel(chatSnapshot, recipient, eventType);
        return new WebSocketEvent(model);
    }
}

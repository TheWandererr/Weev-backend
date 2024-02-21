package com.pivo.weev.backend.domain.service.event.factory;

import static com.pivo.weev.backend.websocket.utils.Constants.PayloadKeys.CHAT;
import static java.util.Collections.singleton;

import com.pivo.weev.backend.domain.model.event.PushNotificationEvent;
import com.pivo.weev.backend.domain.model.event.WebSocketEvent;
import com.pivo.weev.backend.domain.model.event.WebSocketEvent.EventType;
import com.pivo.weev.backend.domain.model.event.payload.ChatSnapshotPayload;
import com.pivo.weev.backend.domain.model.event.payload.UserPayload;
import com.pivo.weev.backend.websocket.utils.Constants.UserDestinations;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class ApplicationEventFactory {

    public PushNotificationEvent buildPushNotificationEvent(Set<UserPayload> recipients, String topic, Map<String, Object> payload) {
        PushNotificationEvent.PushNotificationModel pushNotificationModel = new PushNotificationEvent.PushNotificationModel(recipients, topic, payload);
        return new PushNotificationEvent(pushNotificationModel);
    }

    public PushNotificationEvent buildPushNotificationEvent(UserPayload recipient, String topic, Map<String, Object> payload) {
        return buildPushNotificationEvent(singleton(recipient), topic, payload);
    }

    public WebSocketEvent buildWebSocketEvent(ChatSnapshotPayload payload, String recipient, EventType eventType) {
        WebSocketEvent.WebSocketMessageModel model = new WebSocketEvent.WebSocketMessageModel(eventType, recipient, UserDestinations.UPDATES, Map.of(CHAT, payload));
        return new WebSocketEvent(model);
    }
}

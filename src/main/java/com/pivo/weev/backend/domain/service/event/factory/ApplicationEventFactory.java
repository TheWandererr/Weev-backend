package com.pivo.weev.backend.domain.service.event.factory;

import static com.pivo.weev.backend.websocket.utils.Constants.PayloadKeys.CHAT;
import static java.util.Collections.singleton;

import com.pivo.weev.backend.domain.model.event.PushNotificationEvent;
import com.pivo.weev.backend.domain.model.event.WebSocketMessageEvent;
import com.pivo.weev.backend.domain.model.event.WebSocketMessageEvent.EventType;
import com.pivo.weev.backend.domain.model.messaging.payload.ChatSnapshotPayload;
import com.pivo.weev.backend.domain.model.messaging.payload.UserPayload;
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

    public WebSocketMessageEvent buildWebSocketEvent(ChatSnapshotPayload payload, String recipient, EventType eventType) {
        WebSocketMessageEvent.WebSocketMessageModel model = new WebSocketMessageEvent.WebSocketMessageModel(eventType, recipient, UserDestinations.UPDATES, Map.of(CHAT, payload));
        return new WebSocketMessageEvent(model);
    }
}

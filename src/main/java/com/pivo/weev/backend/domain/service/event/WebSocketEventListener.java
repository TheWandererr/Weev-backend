package com.pivo.weev.backend.domain.service.event;

import static com.pivo.weev.backend.websocket.utils.Constants.PayloadKeys.CHAT;

import com.pivo.weev.backend.domain.model.event.WebSocketEvent;
import com.pivo.weev.backend.domain.model.event.WebSocketEvent.EventType;
import com.pivo.weev.backend.domain.model.event.WebSocketEvent.WebSocketMessageModel;
import com.pivo.weev.backend.domain.model.messaging.chat.CommonChatMessage;
import com.pivo.weev.backend.domain.model.messaging.chat.EventMessage;
import com.pivo.weev.backend.domain.model.messaging.chat.EventMessage.Event;
import com.pivo.weev.backend.websocket.utils.Constants.UserDestinations;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final SimpMessagingTemplate messagingTemplate;

    @Async(value = "webSocketExecutor")
    @EventListener
    public void onWebSocketEventPublishing(WebSocketEvent event) {
        WebSocketMessageModel messageModel = event.getSource();
        if (messageModel.hasRecipient()) {
            CommonChatMessage message = createMessage(messageModel);
            messagingTemplate.convertAndSendToUser(messageModel.getRecipient(), UserDestinations.UPDATES, message);
        }
    }

    private CommonChatMessage createMessage(WebSocketMessageModel messageModel) {
        EventType eventType = messageModel.getEventType();
        return switch (eventType) {
            case CHAT_CREATED -> createEventMessage(messageModel);
            default -> new CommonChatMessage();
        };
    }

    private EventMessage createEventMessage(WebSocketMessageModel messageModel) {
        EventMessage message = new EventMessage();
        message.setEvent(Event.CHAT_CREATED);
        message.setPayload(Map.of(CHAT, messageModel.getChat()));
        return message;
    }
}

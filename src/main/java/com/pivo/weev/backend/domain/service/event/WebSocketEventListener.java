package com.pivo.weev.backend.domain.service.event;

import static com.pivo.weev.backend.websocket.utils.Constants.PayloadKeys.CHAT;

import com.pivo.weev.backend.domain.model.event.WebSocketEvent;
import com.pivo.weev.backend.domain.model.event.WebSocketEvent.EventType;
import com.pivo.weev.backend.domain.model.event.WebSocketEvent.WebSocketMessageModel;
import com.pivo.weev.backend.domain.model.messaging.chat.CommonMessage;
import com.pivo.weev.backend.domain.model.messaging.chat.CommonMessage.Type;
import com.pivo.weev.backend.domain.model.messaging.chat.SubscriptionMessage;
import com.pivo.weev.backend.domain.model.messaging.chat.SubscriptionMessage.Code;
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
            CommonMessage message = createMessage(messageModel);
            messagingTemplate.convertAndSendToUser(messageModel.getRecipient(), UserDestinations.UPDATES, message);
        }
    }

    private CommonMessage createMessage(WebSocketMessageModel messageModel) {
        EventType eventType = messageModel.getEventType();
        return switch (eventType) {
            case CHAT_CREATED -> createSubscriptionMessage(messageModel);
            default -> null;
        };
    }

    private SubscriptionMessage createSubscriptionMessage(WebSocketMessageModel messageModel) {
        SubscriptionMessage message = new SubscriptionMessage();
        message.setType(Type.EVENT);
        message.setCode(Code.CHAT_CREATED);
        message.setPayload(Map.of(CHAT, messageModel.getChat()));
        return message;
    }
}

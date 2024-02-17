package com.pivo.weev.backend.domain.service.event;

import static com.pivo.weev.backend.websocket.utils.Constants.MessageTypes.EVENT;

import com.pivo.weev.backend.domain.model.event.WebSocketEvent;
import com.pivo.weev.backend.domain.model.event.WebSocketEvent.EventType;
import com.pivo.weev.backend.domain.model.event.WebSocketEvent.WebSocketMessageModel;
import com.pivo.weev.backend.websocket.model.MessageWs;
import com.pivo.weev.backend.websocket.utils.Constants.MessageCodes;
import com.pivo.weev.backend.websocket.utils.Constants.UserDestinations;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final SimpMessagingTemplate messagingTemplate;

    @Async(value = "commonExecutor")
    @EventListener
    public void onWebSocketEventPublishing(WebSocketEvent event) {
        WebSocketMessageModel messageModel = event.getSource();
        MessageWs message = createMessage(messageModel);
        messagingTemplate.convertAndSendToUser(messageModel.getRecipient(), UserDestinations.UPDATES, message);
    }

    private MessageWs createMessage(WebSocketMessageModel messageModel) {
        MessageWs message = new MessageWs();
        EventType eventType = messageModel.getEventType();
        message.setType(resolveType(eventType));
        message.setCode(resolveCode(eventType));
        return message;
    }

    private String resolveType(EventType eventType) {
        return switch (eventType) {
            case CHAT_CREATED -> EVENT;
            default -> null;
        };
    }

    private String resolveCode(EventType eventType) {
        return switch (eventType) {
            case CHAT_CREATED -> MessageCodes.CHAT_CREATED;
            default -> null;
        } ;
    }
}

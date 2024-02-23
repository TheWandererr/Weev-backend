package com.pivo.weev.backend.websocket.listener;

import com.pivo.weev.backend.domain.model.event.WebSocketMessageEvent;
import com.pivo.weev.backend.domain.model.event.WebSocketMessageEvent.WebSocketMessageModel;
import com.pivo.weev.backend.websocket.factory.MessageFactory;
import com.pivo.weev.backend.websocket.model.EventMessageWs;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WebSocketMessageEventListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageFactory messageFactory;

    @Async(value = "webSocketExecutor")
    @EventListener
    public void onWebSocketMessageEventPublishing(WebSocketMessageEvent event) {
        WebSocketMessageModel messageModel = event.getSource();
        if (messageModel.hasDestination()) {
            EventMessageWs message = messageFactory.createMessage(messageModel);
            if (messageModel.hasRecipient()) {
                messagingTemplate.convertAndSendToUser(messageModel.getRecipient(), messageModel.getDestination(), message);
            } else {
                messagingTemplate.convertAndSend(messageModel.getDestination(), message);
            }
        }
    }
}

package com.pivo.weev.backend.websocket.listener;

import static com.pivo.weev.backend.websocket.utils.Constants.MessageTypes.EVENT;
import static com.pivo.weev.backend.websocket.utils.Constants.PayloadKeys.CHAT;
import static org.mapstruct.factory.Mappers.getMapper;

import com.pivo.weev.backend.domain.model.event.WebSocketEvent;
import com.pivo.weev.backend.domain.model.event.WebSocketEvent.WebSocketMessageModel;
import com.pivo.weev.backend.websocket.mapping.ws.ChatWsMapper;
import com.pivo.weev.backend.websocket.model.ChatWs;
import com.pivo.weev.backend.websocket.model.EventMessageWs;
import java.util.HashMap;
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
        if (messageModel.hasDestination()) {
            EventMessageWs message = createMessage(messageModel);
            if (messageModel.hasRecipient()) {
                messagingTemplate.convertAndSendToUser(messageModel.getRecipient(), messageModel.getDestination(), message);
            } else {
                messagingTemplate.convertAndSend(messageModel.getDestination(), message);
            }
        }
    }

    private EventMessageWs createMessage(WebSocketMessageModel messageModel) {
        EventMessageWs message = new EventMessageWs();
        message.setEvent(messageModel.getEventType().name());
        message.setType(EVENT);
        message.setPayload(convertPayload(messageModel));
        return message;
    }

    private Map<String, Object> convertPayload(WebSocketMessageModel messageModel) {
        Map<String, Object> payload = new HashMap<>();
        if (messageModel.hasChat()) {
            ChatWs chatWs = getMapper(ChatWsMapper.class).map(messageModel.getChat());
            payload.put(CHAT, chatWs);
        }
        return payload;

    }
}

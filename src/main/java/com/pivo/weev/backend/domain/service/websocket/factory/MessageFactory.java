package com.pivo.weev.backend.domain.service.websocket.factory;

import static com.pivo.weev.backend.domain.model.messaging.chat.EventMessage.Event.SUBSCRIBED;
import static com.pivo.weev.backend.websocket.utils.Constants.PayloadKeys.CHAT;

import com.pivo.weev.backend.domain.model.messaging.chat.ChatSnapshot;
import com.pivo.weev.backend.domain.model.messaging.chat.EventMessage;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class MessageFactory {

    public EventMessage createSubscriptionMessage(String chatId) {
        EventMessage message = new EventMessage();
        message.setEvent(SUBSCRIBED);
        message.setPayload(Map.of(CHAT, new ChatSnapshot(chatId)));
        return message;
    }
}

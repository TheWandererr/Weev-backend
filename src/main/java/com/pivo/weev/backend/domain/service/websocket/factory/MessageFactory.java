package com.pivo.weev.backend.domain.service.websocket.factory;

import static com.pivo.weev.backend.domain.model.messaging.chat.CommonMessage.Type.EVENT;
import static com.pivo.weev.backend.domain.model.messaging.chat.SubscriptionMessage.Code.SUBSCRIBED;
import static com.pivo.weev.backend.websocket.utils.Constants.PayloadKeys.CHAT;

import com.pivo.weev.backend.domain.model.messaging.chat.Chat;
import com.pivo.weev.backend.domain.model.messaging.chat.SubscriptionMessage;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class MessageFactory {

    public SubscriptionMessage createSubscriptionMessage(Long chatId) {
        SubscriptionMessage message = new SubscriptionMessage();
        message.setType(EVENT);
        message.setCode(SUBSCRIBED);
        message.setPayload(Map.of(CHAT, new Chat(chatId)));
        return message;
    }
}

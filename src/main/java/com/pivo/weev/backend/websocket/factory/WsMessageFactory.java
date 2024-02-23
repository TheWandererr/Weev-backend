package com.pivo.weev.backend.websocket.factory;

import static com.pivo.weev.backend.websocket.utils.Constants.MessageTypes.EVENT;
import static com.pivo.weev.backend.websocket.utils.Constants.PayloadKeys.CHAT;
import static org.mapstruct.factory.Mappers.getMapper;

import com.pivo.weev.backend.domain.model.event.WebSocketMessageEvent.WebSocketMessageModel;
import com.pivo.weev.backend.websocket.mapping.ws.ChatWsMapper;
import com.pivo.weev.backend.websocket.model.ChatWs;
import com.pivo.weev.backend.websocket.model.EventMessageWs;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class WsMessageFactory {

    public EventMessageWs createMessage(WebSocketMessageModel messageModel) {
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

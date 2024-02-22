package com.pivo.weev.backend.websocket.mapping.ws;

import com.pivo.weev.backend.domain.model.messaging.chat.ChatMessage;
import com.pivo.weev.backend.domain.model.messaging.chat.EventMessage;
import com.pivo.weev.backend.domain.model.messaging.chat.UserMessage;
import com.pivo.weev.backend.websocket.model.EventMessageWs;
import com.pivo.weev.backend.websocket.model.MessageWs;
import com.pivo.weev.backend.websocket.model.UserMessageWs;
import org.mapstruct.Mapper;
import org.mapstruct.SubclassMapping;

@Mapper(uses = {UserWsMapper.class})
public interface MessageWsMapper {

    @SubclassMapping(target = UserMessageWs.class, source = UserMessage.class)
    @SubclassMapping(target = EventMessageWs.class, source = EventMessage.class)
    MessageWs map(ChatMessage source);
}

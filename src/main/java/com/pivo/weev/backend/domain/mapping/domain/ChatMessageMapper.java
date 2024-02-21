package com.pivo.weev.backend.domain.mapping.domain;

import com.pivo.weev.backend.domain.model.messaging.chat.ChatMessage;
import com.pivo.weev.backend.domain.model.messaging.chat.EventMessage;
import com.pivo.weev.backend.domain.model.messaging.chat.UserMessage;
import com.pivo.weev.backend.integration.mapping.DateTimeMapper;
import com.pivo.weev.backend.websocket.model.EventMessageWs;
import com.pivo.weev.backend.websocket.model.MessageWs;
import com.pivo.weev.backend.websocket.model.UserMessageWs;
import org.mapstruct.Mapper;
import org.mapstruct.SubclassMapping;

@Mapper(uses = {DateTimeMapper.class})
public interface ChatMessageMapper {

    @SubclassMapping(source = UserMessageWs.class, target = UserMessage.class)
    @SubclassMapping(source = EventMessageWs.class, target = EventMessage.class)
    ChatMessage map(MessageWs source);
}

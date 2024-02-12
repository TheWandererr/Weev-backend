package com.pivo.weev.backend.websocket.mapping.ws;

import com.pivo.weev.backend.domain.model.messaging.ChatMessage;
import com.pivo.weev.backend.websocket.model.MessageWs;
import org.mapstruct.Mapper;

@Mapper(uses = {UserWsMapper.class})
public interface MessageWsMapper {

    MessageWs map(ChatMessage source);
}

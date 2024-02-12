package com.pivo.weev.backend.websocket.mapping.ws;

import com.pivo.weev.backend.domain.model.messaging.Chat;
import com.pivo.weev.backend.websocket.model.ChatWs;
import org.mapstruct.Mapper;

@Mapper
public interface ChatWsMapper {

    ChatWs map(Chat chat);
}

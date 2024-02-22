package com.pivo.weev.backend.websocket.mapping.ws;

import com.pivo.weev.backend.domain.model.messaging.chat.ChatUser;
import com.pivo.weev.backend.websocket.model.UserWs;
import org.mapstruct.Mapper;

@Mapper
public interface UserWsMapper {

    UserWs map(ChatUser source);
}

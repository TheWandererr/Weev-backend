package com.pivo.weev.backend.rest.mapping.rest;

import com.pivo.weev.backend.domain.model.messaging.chat.ChatUser;
import com.pivo.weev.backend.rest.model.messaging.ChatUserRest;
import org.mapstruct.Mapper;

@Mapper
public interface ChatUserRestMapper {

    ChatUserRest map(ChatUser source);
}

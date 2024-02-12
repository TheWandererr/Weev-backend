package com.pivo.weev.backend.integration.mapping.domain.chat;

import com.pivo.weev.backend.domain.model.messaging.ChatUser;
import org.mapstruct.Mapper;

@Mapper
public interface ChatUserMapper {

    ChatUser map(com.pivo.weev.backend.integration.firebase.model.chat.ChatUser source);
}

package com.pivo.weev.backend.integration.mapping.firebase.chat;

import com.pivo.weev.backend.integration.firebase.model.chat.ChatUser;
import org.mapstruct.Mapper;

@Mapper
public interface ChatUserMapper {

    ChatUser map(com.pivo.weev.backend.domain.model.messaging.ChatUser source);

    ChatUser map(com.pivo.weev.backend.domain.model.user.User source);
}

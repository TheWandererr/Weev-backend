package com.pivo.weev.backend.integration.mapping.domain.chat;

import com.pivo.weev.backend.domain.model.messaging.chat.ChatUser;
import com.pivo.weev.backend.integration.firebase.model.chat.FirebaseChatUser;
import org.mapstruct.Mapper;

@Mapper
public interface ChatUserMapper {

    ChatUser map(FirebaseChatUser source);
}

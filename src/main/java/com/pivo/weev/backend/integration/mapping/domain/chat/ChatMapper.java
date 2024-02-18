package com.pivo.weev.backend.integration.mapping.domain.chat;

import com.pivo.weev.backend.domain.model.messaging.chat.Chat;
import com.pivo.weev.backend.integration.firebase.model.chat.FirebaseChat;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {CommonChatMessageMapper.class})
public interface ChatMapper {

    @Mapping(target = "messages", ignore = true)
    Chat map(FirebaseChat source);
}

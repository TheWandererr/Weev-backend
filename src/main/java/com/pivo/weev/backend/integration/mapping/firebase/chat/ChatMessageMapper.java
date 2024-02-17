package com.pivo.weev.backend.integration.mapping.firebase.chat;

import com.pivo.weev.backend.integration.firebase.model.chat.ChatMessage;
import com.pivo.weev.backend.integration.mapping.DateTimeMapper;
import org.mapstruct.Mapper;

@Mapper(uses = {ChatUserMapper.class, DateTimeMapper.class})
public interface ChatMessageMapper {

    ChatMessage map(com.pivo.weev.backend.domain.model.messaging.chat.ChatMessage source);
}

package com.pivo.weev.backend.integration.mapping.firebase.chat;

import com.pivo.weev.backend.integration.firebase.model.chat.FirebaseChatMessage;
import com.pivo.weev.backend.integration.mapping.DateTimeMapper;
import org.mapstruct.Mapper;

@Mapper(uses = {FirebaseChatUserMapper.class, DateTimeMapper.class})
public interface FirebaseChatMessageMapper {

    FirebaseChatMessage map(com.pivo.weev.backend.domain.model.messaging.chat.ChatMessage source);
}

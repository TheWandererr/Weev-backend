package com.pivo.weev.backend.integration.mapping.firebase.chat;

import com.pivo.weev.backend.integration.firebase.model.chat.FirebaseChatMessage;
import com.pivo.weev.backend.integration.mapping.DateTimeMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {DateTimeMapper.class})
public interface FirebaseChatMessageMapper {

    @Mapping(target = "from", expression = "java(source.getFrom().getId())")
    FirebaseChatMessage map(com.pivo.weev.backend.domain.model.messaging.chat.ChatMessage source);
}

package com.pivo.weev.backend.integration.mapping.firebase.chat;

import com.pivo.weev.backend.domain.model.messaging.chat.EventMessage;
import com.pivo.weev.backend.domain.model.messaging.chat.UserMessage;
import com.pivo.weev.backend.integration.firebase.model.chat.FirebaseChatMessage;
import com.pivo.weev.backend.integration.mapping.DateTimeMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {DateTimeMapper.class, FirebaseChatUserMapper.class})
public interface FirebaseChatMessageMapper {

    @Mapping(target = "type", expression = "java(source.getType().name())")
    @Mapping(target = "chatId", source = "chatId")
    FirebaseChatMessage map(UserMessage source, String chatId);

    @Mapping(target = "event", expression = "java(source.getEvent().name())")
    @Mapping(target = "chatId", source = "chatId")
    FirebaseChatMessage map(EventMessage source, String chatId);
}

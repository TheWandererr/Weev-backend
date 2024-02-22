package com.pivo.weev.backend.domain.mapping.domain;

import com.pivo.weev.backend.domain.model.event.payload.ChatMessagePayload;
import com.pivo.weev.backend.domain.model.messaging.chat.ChatMessage;
import com.pivo.weev.backend.domain.model.messaging.chat.EventMessage;
import com.pivo.weev.backend.domain.model.messaging.chat.UserMessage;
import org.mapstruct.Mapper;

@Mapper(uses = {UserPayloadMapper.class})
public interface ChatMessagePayloadMapper {

    ChatMessagePayload map(ChatMessage source);

    ChatMessagePayload map(UserMessage source);

    ChatMessagePayload map(EventMessage source);
}

package com.pivo.weev.backend.integration.mapping.domain.chat;

import com.pivo.weev.backend.domain.model.messaging.chat.ChatMessage;
import com.pivo.weev.backend.integration.firebase.model.chat.FirebaseChatMessage;
import com.pivo.weev.backend.integration.mapping.DateTimeMapper;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {DateTimeMapper.class})
public interface ChatMessageMapper {

    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "from", ignore = true)
    ChatMessage map(FirebaseChatMessage source);

    List<ChatMessage> map(List<FirebaseChatMessage> source);
}

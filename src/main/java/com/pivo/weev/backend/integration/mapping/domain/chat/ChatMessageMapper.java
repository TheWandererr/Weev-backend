package com.pivo.weev.backend.integration.mapping.domain.chat;

import static java.util.Objects.isNull;

import com.pivo.weev.backend.domain.model.messaging.WsMessage.Type;
import com.pivo.weev.backend.domain.model.messaging.chat.ChatMessage;
import com.pivo.weev.backend.domain.model.messaging.chat.EventMessage;
import com.pivo.weev.backend.domain.model.messaging.chat.UserMessage;
import com.pivo.weev.backend.integration.firebase.model.chat.FirebaseChatMessage;
import com.pivo.weev.backend.integration.mapping.DateTimeMapper;
import java.util.List;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(uses = {DateTimeMapper.class, ChatUserMapper.class})
public interface ChatMessageMapper {

    @IterableMapping(qualifiedByName = "map")
    List<ChatMessage> map(List<FirebaseChatMessage> source);

    @Named("map")
    default ChatMessage map(FirebaseChatMessage source) {
        if (isNull(source)) {
            return null;
        }
        Type type = Type.valueOf(source.getType());
        return switch (type) {
            case MESSAGE -> mapToUserMessage(source);
            case EVENT -> mapToEventMessage(source);
            default -> null;
        };
    }

    @Mapping(target = "createdAt", source = "createdAt")
    UserMessage mapToUserMessage(FirebaseChatMessage source);

    @Mapping(target = "createdAt", source = "createdAt")
    EventMessage mapToEventMessage(FirebaseChatMessage source);
}

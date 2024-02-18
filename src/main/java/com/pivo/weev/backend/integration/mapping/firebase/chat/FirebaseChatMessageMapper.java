package com.pivo.weev.backend.integration.mapping.firebase.chat;

import static java.util.Optional.ofNullable;

import com.pivo.weev.backend.domain.model.messaging.chat.ChatUser;
import com.pivo.weev.backend.domain.model.messaging.chat.EventMessage;
import com.pivo.weev.backend.domain.model.messaging.chat.UserMessage;
import com.pivo.weev.backend.integration.firebase.model.chat.FirebaseChatMessage;
import com.pivo.weev.backend.integration.mapping.DateTimeMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(uses = {DateTimeMapper.class})
public interface FirebaseChatMessageMapper {

    @Mapping(target = "from", source = "source", qualifiedByName = "extractFrom")
    @Mapping(target = "type", expression = "java(source.getType().name())")
    FirebaseChatMessage map(UserMessage source);

    @Mapping(target = "event", expression = "java(source.getEvent().name())")
    FirebaseChatMessage map(EventMessage source);

    @Named("extractFrom")
    default Long extractFrom(UserMessage source) {
        return ofNullable(source.getFrom())
                .map(ChatUser::getId)
                .orElse(null);
    }
}

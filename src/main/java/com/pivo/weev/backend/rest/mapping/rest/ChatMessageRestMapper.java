package com.pivo.weev.backend.rest.mapping.rest;

import com.pivo.weev.backend.domain.model.messaging.chat.ChatMessage;
import com.pivo.weev.backend.domain.model.messaging.chat.EventMessage;
import com.pivo.weev.backend.domain.model.messaging.chat.UserMessage;
import com.pivo.weev.backend.rest.model.messaging.ChatMessageRest;
import com.pivo.weev.backend.rest.model.messaging.SubscriptionMessageRest;
import com.pivo.weev.backend.rest.model.messaging.UserMessageRest;
import java.util.List;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.SubclassMapping;

@Mapper
public interface ChatMessageRestMapper {

    @Named("map")
    @SubclassMapping(source = UserMessage.class, target = UserMessageRest.class)
    @SubclassMapping(source = EventMessage.class, target = SubscriptionMessageRest.class)
    ChatMessageRest map(ChatMessage chatMessage);

    @IterableMapping(qualifiedByName = "map")
    List<ChatMessageRest> map(List<? extends ChatMessage> chatMessages);
}

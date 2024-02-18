package com.pivo.weev.backend.rest.mapping.rest;

import com.pivo.weev.backend.domain.model.messaging.chat.CommonChatMessage;
import com.pivo.weev.backend.domain.model.messaging.chat.EventMessage;
import com.pivo.weev.backend.domain.model.messaging.chat.UserMessage;
import com.pivo.weev.backend.rest.model.messaging.CommonChatMessageRest;
import com.pivo.weev.backend.rest.model.messaging.SubscriptionMessageRest;
import com.pivo.weev.backend.rest.model.messaging.UserMessageRest;
import java.util.List;
import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.SubclassMapping;

@Mapper
public interface CommonChatMessageRestMapper {

    @Named("map")
    @SubclassMapping(source = UserMessage.class, target = UserMessageRest.class)
    @SubclassMapping(source = EventMessage.class, target = SubscriptionMessageRest.class)
    CommonChatMessageRest map(CommonChatMessage chatMessage);

    @IterableMapping(qualifiedByName = "map")
    List<CommonChatMessageRest> map(List<? extends CommonChatMessage> chatMessages);
}

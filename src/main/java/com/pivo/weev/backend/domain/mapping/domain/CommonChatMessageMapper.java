package com.pivo.weev.backend.domain.mapping.domain;

import com.pivo.weev.backend.domain.model.messaging.chat.CommonChatMessage;
import com.pivo.weev.backend.domain.model.messaging.chat.EventMessage;
import com.pivo.weev.backend.domain.model.messaging.chat.UserMessage;
import com.pivo.weev.backend.integration.mapping.DateTimeMapper;
import com.pivo.weev.backend.websocket.model.CommonMessageWs;
import com.pivo.weev.backend.websocket.model.SubscriptionMessageWs;
import com.pivo.weev.backend.websocket.model.UserMessageWs;
import org.mapstruct.Mapper;
import org.mapstruct.SubclassMapping;

@Mapper(uses = {DateTimeMapper.class})
public interface CommonChatMessageMapper {

    @SubclassMapping(source = UserMessageWs.class, target = UserMessage.class)
    @SubclassMapping(source = SubscriptionMessageWs.class, target = EventMessage.class)
    CommonChatMessage map(CommonMessageWs source);
}

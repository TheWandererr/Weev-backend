package com.pivo.weev.backend.websocket.mapping.ws;

import com.pivo.weev.backend.domain.model.messaging.chat.CommonChatMessage;
import com.pivo.weev.backend.domain.model.messaging.chat.EventMessage;
import com.pivo.weev.backend.domain.model.messaging.chat.UserMessage;
import com.pivo.weev.backend.websocket.model.CommonMessageWs;
import com.pivo.weev.backend.websocket.model.SubscriptionMessageWs;
import com.pivo.weev.backend.websocket.model.UserMessageWs;
import org.mapstruct.Mapper;
import org.mapstruct.SubclassMapping;

@Mapper(uses = {UserWsMapper.class})
public interface CommonMessageWsMapper {

    @SubclassMapping(target = UserMessageWs.class, source = UserMessage.class)
    @SubclassMapping(target = SubscriptionMessageWs.class, source = EventMessage.class)
    CommonMessageWs map(CommonChatMessage source);
}

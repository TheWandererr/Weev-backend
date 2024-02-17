package com.pivo.weev.backend.websocket.mapping.ws;

import com.pivo.weev.backend.domain.model.messaging.chat.ChatMessage;
import com.pivo.weev.backend.domain.model.messaging.chat.CommonMessage;
import com.pivo.weev.backend.domain.model.messaging.chat.SubscriptionMessage;
import com.pivo.weev.backend.websocket.model.ChatMessageWs;
import com.pivo.weev.backend.websocket.model.CommonMessageWs;
import com.pivo.weev.backend.websocket.model.SubscriptionMessageWs;
import org.mapstruct.Mapper;
import org.mapstruct.SubclassMapping;

@Mapper(uses = {UserWsMapper.class})
public interface CommonMessageWsMapper {

    @SubclassMapping(target = ChatMessageWs.class, source = ChatMessage.class)
    @SubclassMapping(target = SubscriptionMessageWs.class, source = SubscriptionMessage.class)
    CommonMessageWs map(CommonMessage source);
}

package com.pivo.weev.backend.domain.mapping.domain;

import com.pivo.weev.backend.domain.model.messaging.chat.ChatMessage;
import com.pivo.weev.backend.domain.model.messaging.chat.CommonMessage;
import com.pivo.weev.backend.domain.model.messaging.chat.SubscriptionMessage;
import com.pivo.weev.backend.websocket.model.ChatMessageWs;
import com.pivo.weev.backend.websocket.model.CommonMessageWs;
import com.pivo.weev.backend.websocket.model.SubscriptionMessageWs;
import org.mapstruct.Mapper;
import org.mapstruct.SubclassMapping;

@Mapper
public interface CommonMessageMapper {

    @SubclassMapping(source = ChatMessageWs.class, target = ChatMessage.class)
    @SubclassMapping(source = SubscriptionMessageWs.class, target = SubscriptionMessage.class)
    CommonMessage map(CommonMessageWs source);
}

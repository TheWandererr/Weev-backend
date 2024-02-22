package com.pivo.weev.backend.websocket.mapping.ws;

import com.pivo.weev.backend.domain.model.messaging.chat.ChatSnapshot;
import com.pivo.weev.backend.domain.model.messaging.payload.ChatSnapshotPayload;
import com.pivo.weev.backend.websocket.model.ChatWs;
import org.mapstruct.Mapper;

@Mapper
public interface ChatWsMapper {

    ChatWs map(ChatSnapshot chatSnapshot);

    ChatWs map(ChatSnapshotPayload source);
}

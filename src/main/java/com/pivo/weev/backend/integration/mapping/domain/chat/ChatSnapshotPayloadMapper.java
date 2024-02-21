package com.pivo.weev.backend.integration.mapping.domain.chat;

import com.pivo.weev.backend.domain.model.event.payload.ChatSnapshotPayload;
import com.pivo.weev.backend.integration.firebase.model.chat.FirebaseChatSnapshot;
import org.mapstruct.Mapper;

@Mapper
public interface ChatSnapshotPayloadMapper {

    ChatSnapshotPayload map(FirebaseChatSnapshot source);
}

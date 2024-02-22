package com.pivo.weev.backend.integration.mapping.domain.chat;

import com.pivo.weev.backend.domain.model.messaging.chat.ChatSnapshot;
import com.pivo.weev.backend.integration.firebase.model.chat.FirebaseChatSnapshot;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {ChatMessageMapper.class})
public interface ChatSnapshotMapper {

    @Mapping(target = "lastMessage", ignore = true)
    ChatSnapshot map(FirebaseChatSnapshot source);
}

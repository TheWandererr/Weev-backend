package com.pivo.weev.backend.rest.mapping.rest;

import com.pivo.weev.backend.domain.model.messaging.chat.ChatSnapshot;
import com.pivo.weev.backend.rest.model.messaging.ChatSnapshotRest;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper(uses = {ChatUserRestMapper.class})
public interface ChatRestMapper {

    ChatSnapshotRest map(ChatSnapshot source);

    List<ChatSnapshotRest> map(List<ChatSnapshot> source);
}

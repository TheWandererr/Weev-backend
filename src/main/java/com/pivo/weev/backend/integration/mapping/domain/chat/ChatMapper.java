package com.pivo.weev.backend.integration.mapping.domain.chat;

import com.pivo.weev.backend.domain.model.messaging.chat.Chat;
import com.pivo.weev.backend.integration.firebase.model.chat.FirebaseChat;
import org.mapstruct.Mapper;

@Mapper
public interface ChatMapper {

    Chat map(FirebaseChat source);
}

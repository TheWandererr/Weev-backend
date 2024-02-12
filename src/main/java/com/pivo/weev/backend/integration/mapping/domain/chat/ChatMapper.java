package com.pivo.weev.backend.integration.mapping.domain.chat;

import com.pivo.weev.backend.domain.model.messaging.Chat;
import org.mapstruct.Mapper;

@Mapper
public interface ChatMapper {

    Chat map(com.pivo.weev.backend.integration.firebase.model.chat.Chat source);
}

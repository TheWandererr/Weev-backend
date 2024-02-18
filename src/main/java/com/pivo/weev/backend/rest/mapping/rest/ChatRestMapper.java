package com.pivo.weev.backend.rest.mapping.rest;

import com.pivo.weev.backend.domain.model.messaging.chat.Chat;
import com.pivo.weev.backend.rest.model.messaging.ChatRest;
import java.util.List;
import org.mapstruct.Mapper;

@Mapper
public interface ChatRestMapper {

    ChatRest map(Chat source);

    List<ChatRest> map(List<Chat> source);
}

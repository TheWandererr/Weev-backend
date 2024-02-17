package com.pivo.weev.backend.domain.mapping.domain;

import com.pivo.weev.backend.domain.model.messaging.chat.ChatUser;
import com.pivo.weev.backend.domain.model.user.User;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserJpa;
import org.mapstruct.Mapper;

@Mapper
public interface ChatUserMapper {

    ChatUser map(User source);

    ChatUser map(UserJpa source);
}

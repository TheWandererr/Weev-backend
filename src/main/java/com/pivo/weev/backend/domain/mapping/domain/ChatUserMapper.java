package com.pivo.weev.backend.domain.mapping.domain;

import com.pivo.weev.backend.domain.model.messaging.chat.ChatUser;
import com.pivo.weev.backend.domain.model.user.User;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserJpa;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ChatUserMapper {

    @Mapping(target = "avatarUrl", source = "avatar.url")
    ChatUser map(User source);

    @Mapping(target = "avatarUrl", source = "avatar.url")
    ChatUser map(UserJpa source);
}

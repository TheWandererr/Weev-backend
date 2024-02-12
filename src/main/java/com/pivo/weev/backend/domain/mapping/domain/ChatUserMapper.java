package com.pivo.weev.backend.domain.mapping.domain;

import com.pivo.weev.backend.domain.model.messaging.ChatUser;
import com.pivo.weev.backend.domain.model.user.User;
import org.mapstruct.Mapper;

@Mapper
public interface ChatUserMapper {

    ChatUser map(User source);
}

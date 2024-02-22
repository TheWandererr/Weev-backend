package com.pivo.weev.backend.domain.mapping.domain;

import com.pivo.weev.backend.domain.model.event.payload.UserPayload;
import com.pivo.weev.backend.domain.model.messaging.chat.ChatUser;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserJpa;
import java.util.Set;
import org.mapstruct.Mapper;

@Mapper(uses = {DevicePayloadMapper.class})
public interface UserPayloadMapper {

    Set<UserPayload> map(Set<UserJpa> source);

    UserPayload map(UserJpa source);

    UserPayload map(ChatUser source);
}

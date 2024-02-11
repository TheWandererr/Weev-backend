package com.pivo.weev.backend.integration.mapping.domain;

import com.pivo.weev.backend.integration.firebase.model.chat.User;
import org.mapstruct.Mapper;

@Mapper
public interface UserMapper {

    User map(com.pivo.weev.backend.domain.model.user.User source);
}

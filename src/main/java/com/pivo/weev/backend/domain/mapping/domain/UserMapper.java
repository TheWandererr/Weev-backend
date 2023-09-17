package com.pivo.weev.backend.domain.mapping.domain;

import com.pivo.weev.backend.domain.model.user.User;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserJpa;
import org.mapstruct.Mapper;

@Mapper(uses = {ImageMapper.class})
public interface UserMapper {

    User map(UserJpa source);
}

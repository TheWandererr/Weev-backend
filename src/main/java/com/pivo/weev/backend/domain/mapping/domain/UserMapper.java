package com.pivo.weev.backend.domain.mapping.domain;

import com.pivo.weev.backend.domain.model.user.User;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserJpa;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {ImageMapper.class, ContactsMapper.class})
public interface UserMapper {

    @Mapping(target = "contacts", source = "source")
    User map(UserJpa source);
}

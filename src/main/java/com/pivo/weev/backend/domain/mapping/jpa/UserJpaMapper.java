package com.pivo.weev.backend.domain.mapping.jpa;

import com.pivo.weev.backend.domain.model.user.UserSnapshot;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserJpa;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface UserJpaMapper {

    @Mapping(target = "email", source = "contacts.email")
    @Mapping(target = "active", constant = "true")
    UserJpa map(UserSnapshot source);
}

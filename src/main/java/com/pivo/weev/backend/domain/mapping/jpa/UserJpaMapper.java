package com.pivo.weev.backend.domain.mapping.jpa;

import com.pivo.weev.backend.domain.model.user.RegisteredUserSnapshot;
import com.pivo.weev.backend.domain.model.user.User;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserJpa;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper
public interface UserJpaMapper {

    @Mapping(target = "email", source = "contacts.email")
    @Mapping(target = "active", constant = "true")
    UserJpa map(RegisteredUserSnapshot source);

    default void update(User source, @MappingTarget UserJpa destination) {
        if (source.hasName()) {
            destination.setName(source.getName());
        }
        if (source.hasNickname()) {
            destination.setNickname(source.getNickname());
        }
    }
}

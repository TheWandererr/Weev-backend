package com.pivo.weev.backend.domain.mapping.jpa;

import com.pivo.weev.backend.domain.model.user.UserSnapshot;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserJpa;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper
public interface UserJpaMapper {

    @Mapping(target = "email", source = "contacts.email")
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "nickname", source = "nickname", qualifiedByName = "formatNickname")
    UserJpa map(UserSnapshot source);

    @Named("formatNickname")
    default String formatNickname(String nickname) {
        return nickname.toLowerCase();
    }
}

package com.pivo.weev.backend.domain.mapping.domain;

import com.pivo.weev.backend.domain.mapping.domain.decorator.UserMapperDecorator;
import com.pivo.weev.backend.domain.model.user.User;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserJpa;
import java.util.Set;
import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@DecoratedWith(UserMapperDecorator.class)
@Mapper(uses = {ImageMapper.class, ContactsMapper.class})
public interface UserMapper {

    @Mapping(target = "contacts", source = "source")
    @Mapping(target = "participatedMeets", expression = "java(source.getParticipatedMeets().size())")
    @Mapping(target = "createdMeets", expression = "java(source.getCreatedMeets().size())")
    User map(UserJpa source);

    Set<User> map(Set<UserJpa> source);
}

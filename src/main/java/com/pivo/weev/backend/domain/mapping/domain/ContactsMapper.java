package com.pivo.weev.backend.domain.mapping.domain;

import com.pivo.weev.backend.domain.model.user.Contacts;
import com.pivo.weev.backend.domain.persistance.jpa.model.user.UserJpa;
import org.mapstruct.Mapper;

@Mapper
public interface ContactsMapper {

    Contacts map(UserJpa source);
}

package com.pivo.weev.backend.rest.mapping.domain;

import com.pivo.weev.backend.domain.model.user.Contacts;
import com.pivo.weev.backend.rest.model.request.RegistrationRequest;
import org.mapstruct.Mapper;

@Mapper
public interface ContactsMapper {

    Contacts map(RegistrationRequest source);
}

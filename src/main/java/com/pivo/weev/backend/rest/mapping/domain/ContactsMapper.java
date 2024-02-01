package com.pivo.weev.backend.rest.mapping.domain;

import com.pivo.weev.backend.domain.model.user.Contacts;
import com.pivo.weev.backend.rest.mapping.UsernameFormatter;
import com.pivo.weev.backend.rest.model.request.RegistrationRequest;
import com.pivo.weev.backend.rest.model.request.VerificationRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {UsernameFormatter.class})
public interface ContactsMapper {

    @Mapping(target = "email", source = "email", qualifiedByName = "formatUsername")
    Contacts map(RegistrationRequest source);

    @Mapping(target = "email", source = "email", qualifiedByName = "formatUsername")
    Contacts map(VerificationRequest source);
}

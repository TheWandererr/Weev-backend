package com.pivo.weev.backend.rest.mapping.domain;

import com.pivo.weev.backend.domain.model.user.RegisteredUserSnapshot;
import com.pivo.weev.backend.rest.mapping.UsernameFormatter;
import com.pivo.weev.backend.rest.model.request.RegistrationRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {ContactsMapper.class, UsernameFormatter.class})
public interface RegisteredUserSnapshotMapper {

    @Mapping(target = "contacts", source = "source")
    @Mapping(target = "nickname", source = "nickname", qualifiedByName = "formatUsername")
    RegisteredUserSnapshot map(RegistrationRequest source);
}

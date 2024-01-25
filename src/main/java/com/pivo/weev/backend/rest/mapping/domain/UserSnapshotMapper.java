package com.pivo.weev.backend.rest.mapping.domain;

import com.pivo.weev.backend.domain.model.user.UserSnapshot;
import com.pivo.weev.backend.rest.mapping.UsernameFormatter;
import com.pivo.weev.backend.rest.model.request.RegistrationRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {ContactsMapper.class, UsernameFormatter.class})
public interface UserSnapshotMapper {

    @Mapping(target = "contacts", source = "source")
    @Mapping(target = "nickname", source = "nickname", qualifiedByName = "formatUsername")
    UserSnapshot map(RegistrationRequest source);
}

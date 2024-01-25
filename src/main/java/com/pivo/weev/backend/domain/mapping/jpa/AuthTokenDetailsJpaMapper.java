package com.pivo.weev.backend.domain.mapping.jpa;

import com.pivo.weev.backend.domain.model.auth.AuthTokenDetails;
import com.pivo.weev.backend.domain.persistance.jpa.model.auth.AuthTokensDetailsJpa;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface AuthTokenDetailsJpaMapper {

    @Mapping(target = "id", ignore = true)
    AuthTokensDetailsJpa map(AuthTokenDetails source);
}

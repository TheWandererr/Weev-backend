package com.pivo.weev.backend.domain.mapping;

import com.pivo.weev.backend.domain.model.auth.OAuthTokenDetails;
import com.pivo.weev.backend.jpa.model.auth.OAuthTokenDetailsJpa;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface OAuthTokenDetailsJpaMapper {

    @Mapping(target = "id", ignore = true)
    OAuthTokenDetailsJpa map(OAuthTokenDetails source);
}

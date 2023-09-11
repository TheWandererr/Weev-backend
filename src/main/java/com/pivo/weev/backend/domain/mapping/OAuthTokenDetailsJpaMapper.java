package com.pivo.weev.backend.domain.mapping;

import com.pivo.weev.backend.dao.model.auth.OAuthTokenDetailsJpa;
import com.pivo.weev.backend.domain.model.auth.OAuthTokenDetails;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface OAuthTokenDetailsJpaMapper {

  @Mapping(target = "id", ignore = true)
  OAuthTokenDetailsJpa map(OAuthTokenDetails source);
}

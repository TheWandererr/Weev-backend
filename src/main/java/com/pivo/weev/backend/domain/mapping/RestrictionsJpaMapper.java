package com.pivo.weev.backend.domain.mapping;

import com.pivo.weev.backend.jpa.model.event.RestrictionsJpa;
import com.pivo.weev.backend.domain.model.event.Restrictions;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface RestrictionsJpaMapper {

  @Mapping(target = "id", ignore = true)
  RestrictionsJpa map(Restrictions source);
}

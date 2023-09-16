package com.pivo.weev.backend.domain.mapping;

import com.pivo.weev.backend.domain.model.event.Restrictions;
import com.pivo.weev.backend.domain.persistance.jpa.model.event.RestrictionsJpa;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface RestrictionsJpaMapper {

    @Mapping(target = "id", ignore = true)
    RestrictionsJpa map(Restrictions source);
}

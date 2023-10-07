package com.pivo.weev.backend.domain.mapping.domain;

import com.pivo.weev.backend.domain.model.event.Restrictions;
import com.pivo.weev.backend.domain.persistance.jpa.model.event.RestrictionsJpa;
import org.mapstruct.Mapper;

@Mapper
public interface RestrictionsMapper {

    Restrictions map(RestrictionsJpa source);
}

package com.pivo.weev.backend.domain.mapping.jpa;

import com.pivo.weev.backend.domain.model.meet.Restrictions;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.RestrictionsJpa;
import org.mapstruct.Mapper;

@Mapper
public interface RestrictionsJpaMapper {

    RestrictionsJpa map(Restrictions source);
}

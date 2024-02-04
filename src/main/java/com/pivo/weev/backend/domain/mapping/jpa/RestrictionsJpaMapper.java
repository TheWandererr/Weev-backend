package com.pivo.weev.backend.domain.mapping.jpa;

import com.pivo.weev.backend.domain.model.meet.Restrictions;
import com.pivo.weev.backend.domain.persistance.jpa.model.meet.RestrictionsJpa;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper
public interface RestrictionsJpaMapper {

    RestrictionsJpa map(Restrictions source);

    @Mapping(target = "id", ignore = true)
    void map(RestrictionsJpa source, @MappingTarget RestrictionsJpa destination);
}

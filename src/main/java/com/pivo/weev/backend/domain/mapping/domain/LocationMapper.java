package com.pivo.weev.backend.domain.mapping.domain;

import com.pivo.weev.backend.domain.model.meet.Location;
import com.pivo.weev.backend.domain.persistance.jpa.model.common.LocationJpa;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {MapPointMapper.class})
public interface LocationMapper {

    @Mapping(target = "point", source = "source")
    Location map(LocationJpa source);
}

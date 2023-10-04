package com.pivo.weev.backend.domain.mapping.domain;

import com.pivo.weev.backend.domain.model.event.Location;
import com.pivo.weev.backend.domain.persistance.jpa.model.event.LocationJpa;
import org.mapstruct.Mapper;

@Mapper(uses = {MapPointMapper.class})
public interface LocationMapper {

    Location map(LocationJpa source);
}

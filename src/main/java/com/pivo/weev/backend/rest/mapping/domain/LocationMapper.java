package com.pivo.weev.backend.rest.mapping.domain;

import com.pivo.weev.backend.domain.model.event.Location;
import com.pivo.weev.backend.rest.model.event.LocationRest;
import org.mapstruct.Mapper;

@Mapper(uses = MapPointMapper.class)
public interface LocationMapper {

    Location map(LocationRest source);
}

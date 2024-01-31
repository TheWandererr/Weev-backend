package com.pivo.weev.backend.rest.mapping.domain;

import com.pivo.weev.backend.domain.model.meet.Location;
import com.pivo.weev.backend.rest.model.meet.LocationRest;
import org.mapstruct.Mapper;

@Mapper(uses = MapPointMapper.class)
public interface LocationMapper {

    Location map(LocationRest source);
}

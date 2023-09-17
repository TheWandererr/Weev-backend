package com.pivo.weev.backend.rest.mapping.rest;

import com.pivo.weev.backend.domain.model.event.Location;
import com.pivo.weev.backend.rest.model.event.LocationRest;
import org.mapstruct.Mapper;

@Mapper
public interface LocationRestMapper {

    LocationRest map(Location source);
}

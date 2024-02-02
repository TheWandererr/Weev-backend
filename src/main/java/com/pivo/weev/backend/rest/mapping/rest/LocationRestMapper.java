package com.pivo.weev.backend.rest.mapping.rest;

import com.pivo.weev.backend.domain.model.meet.Location;
import com.pivo.weev.backend.rest.model.meet.LocationRest;
import org.mapstruct.Mapper;

@Mapper(uses = {MapPointRestMapper.class})
public interface LocationRestMapper {

    LocationRest map(Location source);
}

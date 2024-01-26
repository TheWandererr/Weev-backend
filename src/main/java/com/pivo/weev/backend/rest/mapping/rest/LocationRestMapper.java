package com.pivo.weev.backend.rest.mapping.rest;

import com.pivo.weev.backend.domain.model.event.Location;
import com.pivo.weev.backend.rest.model.event.LocationRest;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {MapPointRestMapper.class})
public interface LocationRestMapper {

    LocationRest map(Location source);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "country", source = "source.country")
    @Mapping(target = "city", source = "source.city")
    LocationRest mapPrivate(Location source);
}

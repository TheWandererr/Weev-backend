package com.pivo.weev.backend.domain.mapping.domain;

import com.pivo.weev.backend.domain.model.event.Location;
import com.pivo.weev.backend.domain.persistance.jpa.model.event.LocationJpa;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface LocationMapper {


    @Mapping(target = "lng", source = "source.point.x")
    @Mapping(target = "ltd", source = "source.point.y")
    Location map(LocationJpa source);
}

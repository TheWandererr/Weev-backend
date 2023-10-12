package com.pivo.weev.backend.domain.mapping.domain;

import static org.mapstruct.factory.Mappers.getMapper;

import com.pivo.weev.backend.domain.model.common.MapPoint;
import com.pivo.weev.backend.domain.model.event.Location;
import com.pivo.weev.backend.domain.persistance.jpa.model.event.LocationJpa;
import com.pivo.weev.backend.rest.mapping.domain.GeoHashMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper
public interface LocationMapper {

    MapPointMapper MAP_POINT_MAPPER = getMapper(MapPointMapper.class);
    GeoHashMapper GEO_HASH_MAPPER = getMapper(GeoHashMapper.class);

    @Mapping(target = "point", source = "source", qualifiedByName = "mapPoint")
    Location map(LocationJpa source);

    @Named("mapPoint")
    default MapPoint mapPoint(LocationJpa source) {
        return MAP_POINT_MAPPER.map(source.getPoint(), GEO_HASH_MAPPER.map(source.getHash()));
    }
}

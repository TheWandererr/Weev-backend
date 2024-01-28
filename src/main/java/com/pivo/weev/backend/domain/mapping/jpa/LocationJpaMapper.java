package com.pivo.weev.backend.domain.mapping.jpa;

import static java.util.Optional.ofNullable;

import com.pivo.weev.backend.domain.model.event.Location;
import com.pivo.weev.backend.domain.persistance.jpa.model.common.LocationJpa;
import com.pivo.weev.backend.domain.persistance.utils.CustomGeometryFactory;
import org.locationtech.jts.geom.Point;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper
public interface LocationJpaMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "point", source = "source", qualifiedByName = "createPoint")
    @Mapping(target = "geoHash", expression = "java(source.getGeoHashString())")
    LocationJpa map(Location source);

    @Named("createPoint")
    default Point createPoint(Location source) {
        return ofNullable(source)
                .map(Location::getPoint)
                .map(point -> CustomGeometryFactory.createPoint(point.getLng(), point.getLtd()))
                .orElse(null);
    }
}

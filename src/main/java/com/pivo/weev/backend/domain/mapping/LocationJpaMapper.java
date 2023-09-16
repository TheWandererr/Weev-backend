package com.pivo.weev.backend.domain.mapping;

import com.pivo.weev.backend.domain.model.event.Location;
import com.pivo.weev.backend.jpa.model.event.LocationJpa;
import com.pivo.weev.backend.jpa.utils.CustomGeometryFactory;
import org.locationtech.jts.geom.Point;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper
public interface LocationJpaMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "point", source = "source", qualifiedByName = "createPoint")
    LocationJpa map(Location source);

    @Named("createPoint")
    default Point createPoint(Location source) {
        return CustomGeometryFactory.createPoint(source.getLng(), source.getLtd());
    }
}

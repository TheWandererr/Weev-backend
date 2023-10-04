package com.pivo.weev.backend.domain.mapping.domain;

import com.pivo.weev.backend.domain.model.common.MapPoint;
import org.locationtech.jts.geom.Point;
import org.mapstruct.Mapper;

@Mapper
public interface MapPointMapper {

    default MapPoint map(Point source) {
        return new MapPoint(source.getX(), source.getY());
    }
}

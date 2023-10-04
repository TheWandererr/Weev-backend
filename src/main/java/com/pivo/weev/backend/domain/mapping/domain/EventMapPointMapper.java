package com.pivo.weev.backend.domain.mapping.domain;

import com.pivo.weev.backend.domain.model.event.EventMapPoint;
import com.pivo.weev.backend.domain.persistance.jpa.model.event.EventJpa;
import java.util.List;
import org.locationtech.jts.geom.Point;
import org.mapstruct.Mapper;

@Mapper
public interface EventMapPointMapper {

    default EventMapPoint map(EventJpa source) {
        EventMapPoint mapPoint = new EventMapPoint();
        mapPoint.setEventId(source.getId());
        Point point = source.getLocation()
                            .getPoint();
        mapPoint.setLng(point.getX());
        mapPoint.setLtd(point.getY());
        return mapPoint;
    }

    List<EventMapPoint> map(List<EventJpa> source);
}

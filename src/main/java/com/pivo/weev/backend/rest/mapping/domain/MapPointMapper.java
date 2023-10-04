package com.pivo.weev.backend.rest.mapping.domain;

import com.pivo.weev.backend.domain.model.common.MapPoint;
import com.pivo.weev.backend.domain.model.event.EventMapPoint;
import com.pivo.weev.backend.rest.model.common.MapPointRest;
import com.pivo.weev.backend.rest.model.event.EventMapPointRest;
import org.mapstruct.Mapper;
import org.mapstruct.SubclassMapping;

@Mapper
public interface MapPointMapper {

    @SubclassMapping(source = EventMapPointRest.class, target = EventMapPoint.class)
    MapPoint map(MapPointRest source);
}

package com.pivo.weev.backend.rest.mapping.rest;

import com.pivo.weev.backend.domain.model.common.MapPoint;
import com.pivo.weev.backend.domain.model.event.EventMapPoint;
import com.pivo.weev.backend.rest.model.common.MapPointRest;
import com.pivo.weev.backend.rest.model.event.EventMapPointRest;
import org.mapstruct.Mapper;
import org.mapstruct.SubclassMapping;

@Mapper
public interface MapPointRestMapper {

    @SubclassMapping(source = EventMapPoint.class, target = EventMapPointRest.class)
    MapPointRest map(MapPoint source);
}

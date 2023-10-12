package com.pivo.weev.backend.rest.mapping.domain;

import com.pivo.weev.backend.domain.model.common.MapPoint;
import com.pivo.weev.backend.rest.model.common.MapPointRest;
import org.mapstruct.Mapper;

@Mapper(uses = {GeoHashMapper.class})
public interface MapPointMapper {

    MapPoint map(MapPointRest source);
}

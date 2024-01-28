package com.pivo.weev.backend.rest.mapping.rest;

import com.pivo.weev.backend.domain.model.common.MapPoint;
import com.pivo.weev.backend.rest.model.common.MapPointRest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface MapPointRestMapper {

    @Mapping(target = "geoHash", expression = "java(source.getGeoHashString())")
    MapPointRest map(MapPoint source);
}

package com.pivo.weev.backend.rest.mapping.rest;


import com.pivo.weev.backend.domain.model.common.MapPointCluster;
import com.pivo.weev.backend.rest.model.common.MapPointClusterRest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {GeoHashRestMapper.class})
public interface MapPointClusterRestMapper {

    @Mapping(target = "size", expression = "java(source.getPoints().size())")
    MapPointClusterRest map(MapPointCluster source);
}

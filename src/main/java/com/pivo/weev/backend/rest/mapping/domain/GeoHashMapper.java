package com.pivo.weev.backend.rest.mapping.domain;

import static ch.hsr.geohash.GeoHash.fromGeohashString;
import static org.apache.commons.lang3.StringUtils.isBlank;

import ch.hsr.geohash.GeoHash;
import org.mapstruct.Mapper;

@Mapper
public interface GeoHashMapper {

    default GeoHash map(String source) {
        return isBlank(source) ? null : fromGeohashString(source);
    }
}

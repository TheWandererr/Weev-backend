package com.pivo.weev.backend.rest.mapping.rest;

import static java.util.Objects.isNull;

import ch.hsr.geohash.GeoHash;
import org.mapstruct.Mapper;

@Mapper
public interface GeoHashRestMapper {

    default String map(GeoHash source) {
        return isNull(source) ? null : source.toBase32();
    }
}

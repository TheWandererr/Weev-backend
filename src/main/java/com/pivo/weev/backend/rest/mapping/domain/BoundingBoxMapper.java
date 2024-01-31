package com.pivo.weev.backend.rest.mapping.domain;

import static java.util.Objects.isNull;

import ch.hsr.geohash.BoundingBox;
import com.pivo.weev.backend.rest.model.common.BoundingBoxRest;
import org.mapstruct.Mapper;

@Mapper
public interface BoundingBoxMapper {

    default BoundingBox map(BoundingBoxRest source) {
        if (isNull(source)) {
            return null;
        }
        return new BoundingBox(
                source.getSouthLatitude(),
                source.getNorthLatitude(),
                source.getWestLongitude(),
                source.getEastLongitude()
        );
    }
}

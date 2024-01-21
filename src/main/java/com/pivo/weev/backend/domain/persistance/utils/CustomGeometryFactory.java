package com.pivo.weev.backend.domain.persistance.utils;

import lombok.experimental.UtilityClass;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;

@UtilityClass
public class CustomGeometryFactory {

    private static final GeometryFactory FACTORY = new GeometryFactory(new PrecisionModel(), 4326);

    public static Point createPoint(Double lng, Double ltd) {
        return FACTORY.createPoint(new Coordinate(lng, ltd));
    }
}

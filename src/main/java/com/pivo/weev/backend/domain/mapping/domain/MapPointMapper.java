package com.pivo.weev.backend.domain.mapping.domain;

import static org.mapstruct.factory.Mappers.getMapper;

import ch.hsr.geohash.GeoHash;
import ch.hsr.geohash.WGS84Point;
import com.pivo.weev.backend.domain.model.common.MapPoint;
import com.pivo.weev.backend.domain.persistance.jpa.model.event.LocationJpa;
import com.pivo.weev.backend.rest.mapping.domain.GeoHashMapper;
import java.util.List;
import org.locationtech.jts.geom.Point;
import org.mapstruct.Mapper;

@Mapper
public interface MapPointMapper {

    GeoHashMapper GEO_HASH_MAPPER = getMapper(GeoHashMapper.class);

    default MapPoint map(Point source, GeoHash hash) {
        return new MapPoint(source.getX(), source.getY(), hash);
    }

    default MapPoint map(LocationJpa source) {
        return map(source.getPoint(), source.getHash());
    }

    default MapPoint map(Point source, String hashString) {
        return map(source, GEO_HASH_MAPPER.map(hashString));
    }

    List<MapPoint> map(List<LocationJpa> source);

    default MapPoint map(String hashString) {
        GeoHash geoHash = GEO_HASH_MAPPER.map(hashString);
        WGS84Point originatingPoint = geoHash.getOriginatingPoint();
        return new MapPoint(originatingPoint.getLongitude(), originatingPoint.getLatitude(), geoHash);
    }
}

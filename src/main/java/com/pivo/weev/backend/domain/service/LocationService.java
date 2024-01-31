package com.pivo.weev.backend.domain.service;

import static ch.hsr.geohash.GeoHash.withCharacterPrecision;
import static org.mapstruct.factory.Mappers.getMapper;

import ch.hsr.geohash.GeoHash;
import com.pivo.weev.backend.domain.mapping.jpa.LocationJpaMapper;
import com.pivo.weev.backend.domain.model.common.MapPoint;
import com.pivo.weev.backend.domain.model.meet.CreatableMeet;
import com.pivo.weev.backend.domain.model.meet.Location;
import com.pivo.weev.backend.domain.persistance.jpa.model.common.LocationJpa;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.LocationRepositoryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepositoryWrapper locationRepository;

    public LocationJpa resolveLocation(CreatableMeet sample) {
        Location location = sample.getLocation();
        MapPoint mapPoint = location.getPoint();
        return locationRepository.findByCoordinates(mapPoint.getLng(), mapPoint.getLtd())
                                 .orElseGet(() -> locationRepository.save(createLocation(location)));
    }

    private LocationJpa createLocation(Location sample) {
        LocationJpa locationJpa = getMapper(LocationJpaMapper.class).map(sample);
        MapPoint point = sample.getPoint();
        GeoHash geoHash = withCharacterPrecision(point.getLtd(), point.getLng(), 12);
        locationJpa.setGeoHash(geoHash.toBase32());
        return locationJpa;
    }
}

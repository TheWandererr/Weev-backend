package com.pivo.weev.backend.domain.service;

import static org.mapstruct.factory.Mappers.getMapper;

import com.pivo.weev.backend.domain.mapping.jpa.LocationJpaMapper;
import com.pivo.weev.backend.domain.model.common.MapPoint;
import com.pivo.weev.backend.domain.model.event.CreatableEvent;
import com.pivo.weev.backend.domain.model.event.Location;
import com.pivo.weev.backend.domain.persistance.jpa.model.event.LocationJpa;
import com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper.LocationRepositoryWrapper;
import java.time.ZoneId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepositoryWrapper locationRepository;

    private final TimeZoneService timeZoneService;

    public ZoneId resolveTimeZone(MapPoint mapPoint) {
        return timeZoneService.getZoneId(mapPoint.getLtd(), mapPoint.getLng());
    }

    public LocationJpa resolveLocation(CreatableEvent sample) {
        Location location = sample.getLocation();
        MapPoint mapPoint = location.getPoint();
        return locationRepository.findByCoordinates(mapPoint.getLng(), mapPoint.getLtd())
                                 .orElseGet(() -> locationRepository.save(getMapper(LocationJpaMapper.class).map(location)));
    }
}

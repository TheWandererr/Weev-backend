package com.pivo.weev.backend.domain.persistance.jpa.repository;

import com.pivo.weev.backend.domain.persistance.jpa.model.event.LocationJpa;
import java.util.Optional;
import org.locationtech.jts.geom.Point;

public interface ILocationRepository extends IGenericRepository<Long, LocationJpa> {

    Optional<LocationJpa> findByPoint(Point point);
}

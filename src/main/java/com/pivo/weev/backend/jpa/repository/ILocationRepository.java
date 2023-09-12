package com.pivo.weev.backend.jpa.repository;

import com.pivo.weev.backend.jpa.model.event.LocationJpa;
import java.util.Optional;
import org.locationtech.jts.geom.Point;

public interface ILocationRepository extends IGenericRepository<Long, LocationJpa> {

  Optional<LocationJpa> findByPoint(Point point);
}

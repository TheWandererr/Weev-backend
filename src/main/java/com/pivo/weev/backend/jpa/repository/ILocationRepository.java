package com.pivo.weev.backend.jpa.repository;

import com.pivo.weev.backend.jpa.model.event.LocationJpa;
import java.util.Optional;

public interface ILocationRepository extends IGenericRepository<Long, LocationJpa> {

  Optional<LocationJpa> findByLngAndLtd(Double lng, Double ltd);
}

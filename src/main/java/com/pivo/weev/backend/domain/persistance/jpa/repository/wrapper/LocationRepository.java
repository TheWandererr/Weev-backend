package com.pivo.weev.backend.domain.persistance.jpa.repository.wrapper;

import static com.pivo.weev.backend.domain.persistance.utils.CustomGeometryFactory.createPoint;

import com.pivo.weev.backend.domain.persistance.jpa.model.common.LocationJpa;
import com.pivo.weev.backend.domain.persistance.jpa.model.common.ResourceName;
import com.pivo.weev.backend.domain.persistance.jpa.repository.ILocationRepository;
import java.util.Optional;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Component;

@Component
public class LocationRepository extends GenericRepository<Long, LocationJpa, ILocationRepository> {

    protected LocationRepository(ILocationRepository repository) {
        super(repository, ResourceName.LOCATION);
    }

    public Optional<LocationJpa> findByCoordinates(Double lng, Double ltd) {
        Point point = createPoint(lng, ltd);
        return repository.findByPoint(point);
    }
}

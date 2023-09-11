package com.pivo.weev.backend.domain.mapping;

import com.pivo.weev.backend.jpa.model.event.LocationJpa;
import com.pivo.weev.backend.domain.model.event.Location;
import org.mapstruct.Mapper;

@Mapper
public interface LocationJpaMapper {

  LocationJpa map(Location source);
}

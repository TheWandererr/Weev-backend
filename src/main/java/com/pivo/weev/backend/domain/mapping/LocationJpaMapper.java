package com.pivo.weev.backend.domain.mapping;

import com.pivo.weev.backend.jpa.model.event.LocationJpa;
import com.pivo.weev.backend.domain.model.event.Location;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface LocationJpaMapper {

  @Mapping(target = "id", ignore = true)
  LocationJpa map(Location source);
}

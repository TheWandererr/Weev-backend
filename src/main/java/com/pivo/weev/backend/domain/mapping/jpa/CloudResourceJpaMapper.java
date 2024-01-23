package com.pivo.weev.backend.domain.mapping.jpa;

import com.pivo.weev.backend.domain.model.common.CloudResource;
import com.pivo.weev.backend.domain.persistance.jpa.model.common.CloudResourceJpa;
import org.mapstruct.Mapper;

@Mapper()
public interface CloudResourceJpaMapper {

    CloudResourceJpa map(CloudResource source);
}

package com.pivo.weev.backend.domain.mapping.domain;

import com.pivo.weev.backend.domain.model.common.Image;
import com.pivo.weev.backend.domain.persistance.jpa.model.common.CloudResourceJpa;
import org.mapstruct.Mapper;

@Mapper
public interface ImageMapper {

    Image map(CloudResourceJpa source);
}

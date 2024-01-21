package com.pivo.weev.backend.domain.mapping.jpa;

import com.pivo.weev.backend.domain.persistance.jpa.model.common.CloudResourceJpa;
import com.pivo.weev.backend.domain.utils.AuthUtils;
import com.pivo.weev.backend.integration.cloudinary.model.Image;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(imports = {AuthUtils.class})
public interface CloudResourceJpaMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "externalId", source = "publicId")
    @Mapping(target = "authorId", expression = "java(AuthUtils.getUserId())")
    CloudResourceJpa map(Image source);
}

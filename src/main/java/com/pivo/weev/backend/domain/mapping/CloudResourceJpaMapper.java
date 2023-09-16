package com.pivo.weev.backend.domain.mapping;

import com.pivo.weev.backend.domain.utils.AuthUtils;
import com.pivo.weev.backend.integration.client.cloudinary.model.Image;
import com.pivo.weev.backend.domain.persistance.jpa.model.common.CloudResourceJpa;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(imports = {AuthUtils.class})
public interface CloudResourceJpaMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "externalId", source = "source.publicId")
    @Mapping(target = "authorId", expression = "java(AuthUtils.getUserId())")
    CloudResourceJpa map(Image source);
}

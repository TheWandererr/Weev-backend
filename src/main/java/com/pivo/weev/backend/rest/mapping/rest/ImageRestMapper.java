package com.pivo.weev.backend.rest.mapping.rest;

import com.pivo.weev.backend.domain.model.common.Image;
import com.pivo.weev.backend.rest.model.event.ImageRest;
import org.mapstruct.Mapper;

@Mapper
public interface ImageRestMapper {

    ImageRest map(Image source);
}

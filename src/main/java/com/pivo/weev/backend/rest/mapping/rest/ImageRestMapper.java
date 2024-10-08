package com.pivo.weev.backend.rest.mapping.rest;

import com.pivo.weev.backend.domain.model.common.Image;
import com.pivo.weev.backend.rest.model.meet.ImageRest;
import org.mapstruct.Mapper;

@Mapper
public interface ImageRestMapper {

    ImageRest map(Image source);

    default ImageRest map(String source) {
        return new ImageRest(source);
    }
}

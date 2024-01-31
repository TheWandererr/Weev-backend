package com.pivo.weev.backend.domain.mapping.domain;

import com.pivo.weev.backend.domain.persistance.jpa.model.meet.CategoryJpa;
import org.mapstruct.Mapper;

@Mapper
public interface CategoryMapper {

    default String map(CategoryJpa source) {
        return source.toString();
    }
}

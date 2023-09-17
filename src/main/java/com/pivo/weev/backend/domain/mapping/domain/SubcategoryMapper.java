package com.pivo.weev.backend.domain.mapping.domain;

import com.pivo.weev.backend.domain.persistance.jpa.model.event.SubcategoryJpa;
import org.mapstruct.Mapper;

@Mapper
public interface SubcategoryMapper {

    default String map(SubcategoryJpa source) {
        return source.toString();
    }
}

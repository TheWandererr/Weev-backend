package com.pivo.weev.backend.rest.mapping.rest;

import com.pivo.weev.backend.domain.model.event.Restrictions;
import com.pivo.weev.backend.rest.model.event.RestrictionsRest;
import org.mapstruct.Mapper;

@Mapper
public interface RestrictionsRestMapper {

    RestrictionsRest map(Restrictions source);
}

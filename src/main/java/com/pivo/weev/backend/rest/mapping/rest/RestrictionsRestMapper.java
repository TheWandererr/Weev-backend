package com.pivo.weev.backend.rest.mapping.rest;

import com.pivo.weev.backend.domain.model.meet.Restrictions;
import com.pivo.weev.backend.rest.model.meet.RestrictionsRest;
import org.mapstruct.Mapper;

@Mapper
public interface RestrictionsRestMapper {

    RestrictionsRest map(Restrictions source);
}

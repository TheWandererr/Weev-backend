package com.pivo.weev.backend.rest.mapping.domain;

import com.pivo.weev.backend.domain.model.common.InstantPeriod;
import com.pivo.weev.backend.rest.model.common.InstantPeriodRest;
import org.mapstruct.Mapper;

@Mapper
public interface PeriodMapper {

    InstantPeriod map(InstantPeriodRest source);
}

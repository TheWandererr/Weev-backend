package com.pivo.weev.backend.rest.mapping.rest;

import com.pivo.weev.backend.domain.model.user.MeetsStatistics;
import com.pivo.weev.backend.rest.model.user.MeetsStatisticsRest;
import org.mapstruct.Mapper;

@Mapper
public interface MeetsStatisticsMapper {

    MeetsStatisticsRest map(MeetsStatistics source);
}

package com.pivo.weev.backend.domain.mapping.domain;

import com.pivo.weev.backend.domain.model.user.MeetsStatistics;
import com.pivo.weev.backend.domain.persistance.jpa.projection.MeetsStatisticsProjection;
import org.mapstruct.Mapper;

@Mapper
public interface MeetsStatisticsMapper {

    MeetsStatistics map(MeetsStatisticsProjection source);
}

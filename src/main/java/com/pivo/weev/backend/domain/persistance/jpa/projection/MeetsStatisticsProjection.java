package com.pivo.weev.backend.domain.persistance.jpa.projection;

import com.pivo.weev.backend.domain.persistance.jpa.model.common.Entity;

public interface MeetsStatisticsProjection extends Entity {

    Long getCreated();

    Long getParticipated();
}

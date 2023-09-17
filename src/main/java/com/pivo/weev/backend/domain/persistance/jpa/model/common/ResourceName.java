package com.pivo.weev.backend.domain.persistance.jpa.model.common;

import com.pivo.weev.backend.common.utils.Enumerated;

public enum ResourceName implements Enumerated {

    USER,
    OAUTH_TOKEN_DETAILS,
    EVENT,
    EVENT_CATEGORY,
    LOCATION,
    NOTIFICATION,
    DECLINATION_REASON;

    @Override
    public String getName() {
        return name();
    }
}

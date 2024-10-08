package com.pivo.weev.backend.domain.persistance.jpa.model.common;

import com.pivo.weev.backend.utils.Enumerated;

public enum ResourceName implements Enumerated {

    USER,
    DEVICE,
    USER_ROLE,
    AUTH_TOKEN_DETAILS,
    MEET,
    MEET_TEMPLATE,
    MEET_CATEGORY,
    MEET_REQUEST,
    LOCATION,
    NOTIFICATION,
    DECLINATION_REASON,
    CLOUD_RESOURCE,
    VERIFICATION_REQUEST,
    CONFIG;

    @Override
    public String getName() {
        return name();
    }
}

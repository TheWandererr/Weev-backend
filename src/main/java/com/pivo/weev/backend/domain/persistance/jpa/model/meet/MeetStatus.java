package com.pivo.weev.backend.domain.persistance.jpa.model.meet;

import lombok.Getter;

@Getter
public enum MeetStatus {
    ON_MODERATION("ON_MODERATION"),
    HAS_MODERATION_INSTANCE("HAS_MODERATION_INSTANCE"),
    CONFIRMED("CONFIRMED"),
    DECLINED("DECLINED"),
    CANCELED("CANCELED"),
    DELETED("DELETED");

    private final String value;

    MeetStatus(String value) {
        this.value = value;
    }

}

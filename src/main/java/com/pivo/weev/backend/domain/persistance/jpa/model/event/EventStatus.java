package com.pivo.weev.backend.domain.persistance.jpa.model.event;

import lombok.Getter;

@Getter
public enum EventStatus {
    ON_MODERATION("ON_MODERATION"),
    HAS_MODERATION_INSTANCE("HAS_MODERATION_INSTANCE"),
    CONFIRMED("CONFIRMED"),
    DECLINED("DECLINED"),
    CANCELED("CANCELED"),
    DELETED("DELETED");

    private final String value;

    EventStatus(String value) {
        this.value = value;
    }

}

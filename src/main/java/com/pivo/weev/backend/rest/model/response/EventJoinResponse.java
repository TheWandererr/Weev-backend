package com.pivo.weev.backend.rest.model.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventJoinResponse {

    private boolean joined;

    public EventJoinResponse(boolean joined) {
        this.joined = joined;
    }
}

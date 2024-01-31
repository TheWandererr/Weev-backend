package com.pivo.weev.backend.rest.model.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MeetJoinResponse {

    private boolean joined;

    public MeetJoinResponse(boolean joined) {
        this.joined = joined;
    }
}

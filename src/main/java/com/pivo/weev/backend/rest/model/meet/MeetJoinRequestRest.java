package com.pivo.weev.backend.rest.model.meet;

import com.pivo.weev.backend.rest.model.user.UserSnapshotRest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MeetJoinRequestRest {

    private UserSnapshotRest joiner;
    private MeetSnapshotRest meet;
}

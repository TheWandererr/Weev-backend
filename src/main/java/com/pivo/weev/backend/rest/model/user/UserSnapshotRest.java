package com.pivo.weev.backend.rest.model.user;

import com.pivo.weev.backend.rest.model.meet.ImageRest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSnapshotRest {

    private Long id;
    private String nickname;
    private ImageRest avatar;
    private boolean deleted;
}

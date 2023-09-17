package com.pivo.weev.backend.rest.model;

import com.pivo.weev.backend.rest.model.event.ImageRest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCompactedRest {

    private Long id;
    private String nickname;
    private ImageRest avatar;
}

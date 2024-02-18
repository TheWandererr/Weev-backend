package com.pivo.weev.backend.rest.model.messaging;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatUserRest {

    private Long id;
    private String nickname;
    private String avatarUrl;
    private boolean deleted;
}

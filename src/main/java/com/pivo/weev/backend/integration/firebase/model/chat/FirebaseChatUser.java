package com.pivo.weev.backend.integration.firebase.model.chat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FirebaseChatUser {

    private Long id;
    private String nickname;
    private String avatarUrl;
}

package com.pivo.weev.backend.domain.model.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisteredUserSnapshot {

    private Contacts contacts;
    private String nickname;
    private String password;
}

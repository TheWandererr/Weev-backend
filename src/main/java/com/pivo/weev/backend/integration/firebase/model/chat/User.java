package com.pivo.weev.backend.integration.firebase.model.chat;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User implements Serializable {

    private long id;
    private String nickname;
}

package com.pivo.weev.backend.integration.firebase.model.chat;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FirebaseChatUser implements Serializable {

    private long id;
    private String nickname;
}

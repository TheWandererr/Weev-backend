package com.pivo.weev.backend.domain.model.messaging.chat;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatUser implements Serializable {

    private long id;
    private String nickname;
    private String avatarUrl;
}

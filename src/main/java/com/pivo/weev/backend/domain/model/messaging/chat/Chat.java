package com.pivo.weev.backend.domain.model.messaging.chat;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Chat implements Serializable {

    private Long id;
    private String creatorId;
    private String name;
    private String avatarUrl;

    public Chat(Long id) {
        this.id = id;
    }
}

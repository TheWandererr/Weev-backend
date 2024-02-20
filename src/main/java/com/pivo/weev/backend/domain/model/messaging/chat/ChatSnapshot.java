package com.pivo.weev.backend.domain.model.messaging.chat;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatSnapshot implements Serializable {

    private String id;
    private Long creatorId;
    private String name;
    private String avatarUrl;
    private CommonChatMessage lastMessage;
    private Integer users;
    private Long newMessages;

    public ChatSnapshot(String id) {
        this.id = id;
    }
}

package com.pivo.weev.backend.domain.model.messaging.chat;

import static java.util.Objects.isNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Chat implements Serializable {

    private String id;
    private Long creatorId;
    private String name;
    private String avatarUrl;
    private List<CommonChatMessage> messages;
    private Integer users;

    public Chat(String id) {
        this.id = id;
    }

    public List<CommonChatMessage> getMessages() {
        if (isNull(messages)) {
            messages = new ArrayList<>();
        }
        return messages;
    }
}

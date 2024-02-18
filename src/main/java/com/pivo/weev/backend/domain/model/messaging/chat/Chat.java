package com.pivo.weev.backend.domain.model.messaging.chat;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Chat implements Serializable {

    private Long id;
    private Long creatorId;
    private String name;
    private String avatarUrl;
    private List<ChatMessage> messages;
    private Integer users;

    public Chat(Long id) {
        this.id = id;
    }

    public List<ChatMessage> getMessages() {
        if (isNull(messages)) {
            messages = new ArrayList<>();
        }
        return messages;
    }

    public void addMessage(ChatMessage chatMessage) {
        if (nonNull(chatMessage)) {
            getMessages().add(chatMessage);
        }
    }
}

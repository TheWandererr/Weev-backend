package com.pivo.weev.backend.rest.model.messaging;

import static java.util.Objects.isNull;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatRest {

    private Long id;
    private String name;
    private String avatarUrl;
    private List<ChatMessageRest> messages;
    private Integer users;

    public List<ChatMessageRest> getMessages() {
        if (isNull(messages)) {
            messages = new ArrayList<>();
        }
        return messages;
    }
}

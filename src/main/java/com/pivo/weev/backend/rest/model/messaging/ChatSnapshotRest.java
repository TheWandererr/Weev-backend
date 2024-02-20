package com.pivo.weev.backend.rest.model.messaging;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatSnapshotRest {

    private String id;
    private String name;
    private String avatarUrl;
    private UserMessageRest lastMessage;
    private Integer users;
    private Integer newMessages;
}

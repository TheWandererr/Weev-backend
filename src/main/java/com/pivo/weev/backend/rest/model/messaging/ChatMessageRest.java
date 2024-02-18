package com.pivo.weev.backend.rest.model.messaging;

import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessageRest {

    private String text;
    private Instant createdAt;
    private ChatUserRest from;
}

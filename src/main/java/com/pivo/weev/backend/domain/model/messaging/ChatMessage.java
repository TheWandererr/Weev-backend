package com.pivo.weev.backend.domain.model.messaging;

import static com.pivo.weev.backend.websocket.utils.Constants.MessageTypes.EVENT;

import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessage {

    private String text;
    private String code;
    private String type;
    private ChatUser from;
    private Instant createdAt = Instant.now();

    public boolean isEvent() {
        return EVENT.equals(type);
    }
}

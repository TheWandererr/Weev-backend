package com.pivo.weev.backend.domain.model.messaging;

import static com.pivo.weev.backend.utils.Constants.WebSocketParams.MessageTypes.EVENT;

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

    public boolean isSystem() {
        return EVENT.equals(type);
    }
}

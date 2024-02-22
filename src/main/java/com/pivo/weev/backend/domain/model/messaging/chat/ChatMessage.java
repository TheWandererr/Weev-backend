package com.pivo.weev.backend.domain.model.messaging.chat;


import static com.pivo.weev.backend.domain.model.messaging.chat.ChatMessage.Type.EVENT;
import static com.pivo.weev.backend.domain.model.messaging.chat.ChatMessage.Type.UNDEFINED;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessage {

    private String chatId;
    private String text;
    private Instant createdAt = Instant.now();
    private Map<String, Object> payload = new HashMap<>();
    private Long ordinal;

    public boolean isEvent() {
        return EVENT == getType();
    }

    public enum Type {
        EVENT,
        MESSAGE,
        UNDEFINED
    }

    protected Type getType() {
        return UNDEFINED;
    }
}

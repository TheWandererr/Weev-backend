package com.pivo.weev.backend.domain.model.messaging.chat;


import static com.pivo.weev.backend.domain.model.messaging.chat.CommonMessage.Type.EVENT;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommonMessage {

    private String text;
    private Type type;
    private Instant createdAt = Instant.now();
    private Map<String, ? extends Serializable> payload = new HashMap<>();

    public boolean isEvent() {
        return EVENT == type;
    }

    public enum Type {
        EVENT,
        MESSAGE
    }
}

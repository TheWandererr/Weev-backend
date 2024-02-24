package com.pivo.weev.backend.domain.model.messaging;

import static com.pivo.weev.backend.domain.model.messaging.CommonMessage.Type.EVENT;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class CommonMessage {

    private String text;
    private Instant createdAt = Instant.now();
    private Map<String, Object> payload = new HashMap<>();

    public boolean isEvent() {
        return EVENT == getType();
    }

    public enum Type {
        EVENT,
        MESSAGE,
        UNDEFINED
    }

    protected abstract Type getType();

}

package com.pivo.weev.backend.domain.model.messaging.payload;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessagePayload {

    private UserPayload from;
    private String type;
    private String event;
    private String chatId;
    private String text;
    private Instant createdAt;
    private Map<String, Object> payload = new HashMap<>();
    private Long ordinal;
}

package com.pivo.weev.backend.websocket.model;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MessageWs {

    private String type;
    private String text;
    private Instant createdAt = Instant.now();
    private Map<String, Object> payload = new HashMap<>();
}

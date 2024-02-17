package com.pivo.weev.backend.websocket.model;

import java.io.Serializable;
import java.time.Instant;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MessageWs implements Serializable {

    private String text;
    private String code;
    private String type;
    private UserWs from;
    private Instant createdAt = Instant.now();

    public MessageWs(String text) {
        this.text = text;
    }
}

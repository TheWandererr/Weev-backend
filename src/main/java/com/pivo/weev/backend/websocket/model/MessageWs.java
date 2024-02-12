package com.pivo.weev.backend.websocket.model;

import java.io.Serializable;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageWs implements Serializable {

    private String text;
    private String code;
    private String type;
    private UserWs from;
    private Instant createdAt = Instant.now();
}

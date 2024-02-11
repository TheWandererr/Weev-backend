package com.pivo.weev.backend.integration.firebase.model.chat;

import java.io.Serializable;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Message implements Serializable {

    private String text;
    private User sender;
    private Instant timestamp;
}

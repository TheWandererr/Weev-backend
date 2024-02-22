package com.pivo.weev.backend.websocket.model;

import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatWs implements Serializable {

    private Long id;
    private String creatorId;
    private String name;
    private String avatarUrl;
    private Integer users;
    private Long lastUpdate;
}

package com.pivo.weev.backend.integration.firebase.model.chat;

import static java.time.Instant.now;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FirebaseChatSnapshot {

    private String id;
    private Long creatorId;
    private String name;
    private String avatarUrl;
    private Integer users = 1;
    private Long lastUpdate = now().toEpochMilli();
}

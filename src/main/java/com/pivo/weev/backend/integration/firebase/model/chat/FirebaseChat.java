package com.pivo.weev.backend.integration.firebase.model.chat;

import static java.time.Instant.now;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FirebaseChat {

    private String id;
    private Long creatorId;
    private String name;
    private String avatarUrl;
    private Integer users = 1;
    private List<FirebaseChatMessage> messages = new ArrayList<>();
    private Long lastUpdate = now().toEpochMilli();

    private void updated() {
        setLastUpdate(now().toEpochMilli());
    }

    public void addMessage(FirebaseChatMessage message) {
        getMessages().add(message);
        updated();
    }
}

package com.pivo.weev.backend.integration.firebase.model.chat;

import static java.util.Objects.nonNull;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FirebaseChat {

    private Long id;
    private String creatorId;
    private String name;
    private String avatarUrl;
    private List<FirebaseChatMessage> messages = new ArrayList<>();
    private List<FirebaseChatUser> users = new ArrayList<>();

    public void addUser(FirebaseChatUser user) {
        if (nonNull(user)) {
            getUsers().add(user);
        }
    }

    public void addMessage(FirebaseChatMessage message) {
        if (nonNull(message)) {
            getMessages().add(message);
        }
    }
}

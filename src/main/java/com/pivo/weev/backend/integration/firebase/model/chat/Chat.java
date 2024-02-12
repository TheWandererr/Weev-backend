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
public class Chat {

    private Long id;
    private String creatorId;
    private String name;
    private String avatarUrl;
    private List<ChatMessage> messages = new ArrayList<>();
    private List<ChatUser> users = new ArrayList<>();

    public void addUser(ChatUser user) {
        if (nonNull(user)) {
            getUsers().add(user);
        }
    }

    public void addMessage(ChatMessage message) {
        if (nonNull(message)) {
            getMessages().add(message);
        }
    }
}

package com.pivo.weev.backend.integration.firebase.model.chat;

import static java.util.Objects.nonNull;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Chat {

    private String id;
    private String creatorId;
    private String topic;
    private String avatarUrl;
    private List<Message> messages = new ArrayList<>();
    private List<User> users = new ArrayList<>();

    public void addUser(User user) {
        if (nonNull(user)) {
            getUsers().add(user);
        }
    }
}

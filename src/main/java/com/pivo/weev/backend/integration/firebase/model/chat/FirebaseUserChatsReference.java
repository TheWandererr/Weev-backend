package com.pivo.weev.backend.integration.firebase.model.chat;

import static java.util.Objects.isNull;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FirebaseUserChatsReference {

    private Long userId;
    private List<String> chatIds;

    public List<String> getChatIds() {
        if (isNull(chatIds)) {
            chatIds = new ArrayList<>();
        }
        return chatIds;
    }

    public void addReference(String chatId) {
        if (!getChatIds().contains(chatId)) {
            getChatIds().add(chatId);
        }
    }
}
